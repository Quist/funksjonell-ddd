package PlasserBestillingWorkflow

import PlasserBestillingWorkflow.IkkeValidertBestilling.IkkeValidertOrdrelinje
import com.github.michaelbull.result.*


// ==================================
// Implementasjon av PlasserBestillingWorkflow
// ==================================
fun initializePlasserBestillingWorkflow(
    sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer,     // Dependency
    sjekkAdresseEksisterer: SjekkAdresseEksisterer,             // Dependency
    hentProduktPris: HentProduktPris,                           // Dependency
    lagBekreftelsesEpostHtml: LagBekreftelsesEpostHtml,         // Dependency
    sendBekreftelsesEpost: SendBekreftelsesEpost                // Dependency
): PlasserBestillingWorkflow {
    return fun(bestilling: Bestilling): Result<PlasserBestillingHendelser, Valideringsfeil> {
        return validerBestilling(
            sjekkProduktKodeEksisterer, sjekkAdresseEksisterer, bestilling.bestilling
        )
            .andThen { prisOrdre(hentProduktPris, it) }
            .andThen { bekreftBestilling(lagBekreftelsesEpostHtml, sendBekreftelsesEpost, it) }
            .andThen { Ok(lagHendelser(it.prisetBestilling, it.sendEpostResultat)) }
    }
}

// ==================================
// Valider bestilling steg
// ==================================
private fun validerBestilling(
    sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer, // Dependency
    sjekkAdresseEksisterer: SjekkAdresseEksisterer,         // Dependency
    bestilling: IkkeValidertBestilling                      // Input
): Result<ValidertBestilling, Nothing> {
    val ordreId = OrdreId.of(bestilling.ordreId)
    val leveringsadresse = tilValidertAdresse(sjekkAdresseEksisterer, bestilling.leveringsadresse)
    val fakturadresse = tilValidertAdresse(sjekkAdresseEksisterer, bestilling.fakturadresse)
    val ordrelinjer = tilValiderteOrdrelinjer(tilProduktKode(sjekkProduktKodeEksisterer), bestilling.ordrelinjer)
    val kundeInfo = KundeInfo(
        kundeId = KundeId.of(bestilling.kundeId),
        kundeEpost = bestilling.kundeEpost
    )

    return Ok(
        ValidertBestilling(
            ordreId = ordreId,
            fakturaAdresse = fakturadresse,
            leveringsadresse = leveringsadresse,
            kundeInfo = kundeInfo,
            ordrelinjer = ordrelinjer,
        )
    )
}

private fun tilValiderteOrdrelinjer(
    tilProduktKode: (String) -> Produktkode, ordrelinjer: List<IkkeValidertOrdrelinje>
): List<ValidertOrdrelinje> {
    return (ordrelinjer.map {
        tilValidertValidertOrdrelinje(tilProduktKode, it)
    })
}

private fun tilValidertValidertOrdrelinje(
    tilProduktKode: (String) -> Produktkode, // Dependency
    ordrelinje: IkkeValidertOrdrelinje // Input
): ValidertOrdrelinje {
    return ValidertOrdrelinje(
        produktkode = tilProduktKode(ordrelinje.produktkode),
        ordreMengde = OrdreMengde.Enhet(Enhetsmengde(ordrelinje.mengde)),
    )
}

private fun tilProduktKode(sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer): (String) -> Produktkode.Klatreutstyr {
    return { produktKode: String ->
        if (!sjekkProduktKodeEksisterer(produktKode)) { // Kall eksterne tjeneste
            throw UgyldigOrdreException("Ugyldig kode: Produktet '$produktKode' eksisterer ikke")
        }
        Produktkode.Klatreutstyr(KlatreutstyrKode(produktKode))
    }
}

private fun tilValidertAdresse(
    sjekkAdresseEksisterer: SjekkAdresseEksisterer,
    adresse: IkkeValidertBestilling.IkkeValidertAdresse
): ValidertAdresse {
    // Sjekk om både gateadresse og postnummer er ikke-null og validere dem
    if (adresse.gateadresse.isNullOrEmpty() || adresse.postnummer.isNullOrEmpty()) {
        throw UgyldigAdresse("Gateadresse eller postnummer er null eller tom: $adresse")
    }

    // TODO Oppgave 1c: Husk å faktisk sjekk om adressen finnes!
    return ValidertAdresse(adresse.gateadresse, adresse.postnummer.toInt())
}

data class ValidertBestilling(
    val ordreId: OrdreId,
    val kundeInfo: KundeInfo,
    val leveringsadresse: ValidertAdresse,
    val fakturaAdresse: ValidertAdresse,
    val ordrelinjer: List<ValidertOrdrelinje>,
)

data class ValidertOrdrelinje(
    val produktkode: Produktkode,
    val ordreMengde: OrdreMengde,
)

data class ValidertAdresse(val gateadresse: String, val postnummer: Number)
data class KundeInfo(val kundeId: KundeId, val kundeEpost: String)

// ==================================
// Pris bestilling steg
// ==================================
private fun prisOrdre(
    getProduktPris: HentProduktPris,        // Dependency
    validertBestilling: ValidertBestilling  // Input
): Result<PrisetBestilling, String> {
    val faktura =
        validertBestilling.ordrelinjer.map { prisOrdreLinje(getProduktPris, it) }
            .sumOf { it.toDouble() }
            .let { Pris.of(it) }
    return Ok(
        PrisetBestilling(
            ordreId = validertBestilling.ordreId,
            fakturaadresse = validertBestilling.fakturaAdresse,
            kundeInfo = validertBestilling.kundeInfo,
            leveringsadresse = validertBestilling.leveringsadresse,
            fakturaSum = faktura
        )
    )
}

private fun prisOrdreLinje(getProduktPris: HentProduktPris, ordrelinje: ValidertOrdrelinje): Number {
    val quantity: Int = when (ordrelinje.ordreMengde) { // TODO Remove this structure
        is OrdreMengde.Enhet -> ordrelinje.ordreMengde.mengde.value.toInt()
        is OrdreMengde.Kilo -> ordrelinje.ordreMengde.mengde.value.toInt()
    }
    return (quantity * getProduktPris(ordrelinje.produktkode));
}

// ==================================
// Bekreft bestilling steg
// ==================================
private fun bekreftBestilling(
    lagBekreftelsesEpostHtml: LagBekreftelsesEpostHtml,  // Dependency
    sendBekreftelsesEpost: SendBekreftelsesEpost,        // Dependency
    prisetBestilling: PrisetBestilling                   // Input
): Result<BekreftetBestilling, String> {
    lagBekreftelsesEpostHtml(prisetBestilling).let { letter ->
        return Ok(
            BekreftetBestilling(
                sendBekreftelsesEpost(prisetBestilling.kundeInfo.kundeEpost, letter),
                prisetBestilling
            )
        )
    }
}

data class BekreftetBestilling(val sendEpostResultat: SendEpostResultat, val prisetBestilling: PrisetBestilling)

// ==================================
// Lag hendelser
// ==================================
private fun lagHendelser(
    prisetBestilling: PrisetBestilling,
    sendEpostResultat: SendEpostResultat
): PlasserBestillingHendelser {
    return PlasserBestillingHendelser(
        bekreftelseSent = sendEpostResultat,
        ordrePlassert = prisetBestilling,
        fakturerbarOrdrePlassert = lagFakturerBarHendelse(prisetBestilling)
    )
}

private fun lagFakturerBarHendelse(prisetBestilling: PrisetBestilling): FakturerbarOrdrePlassert? {
    return if (prisetBestilling.fakturaSum.value.toDouble() > 0) {
        FakturerbarOrdrePlassert(
            ordreId = prisetBestilling.ordreId,
            fakturadresse = prisetBestilling.fakturaadresse,
            fakturasum = prisetBestilling.fakturaSum
        )
    } else null
}