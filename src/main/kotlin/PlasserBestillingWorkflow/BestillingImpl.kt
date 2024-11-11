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
    return fun(bestilling: Bestilling): Result<List<PlasserBestillingHendelse>, Valideringsfeil> {
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
        ordreMengde = Mengde.of(ordrelinje.mengde.toInt()),
    )
}

private fun tilProduktKode(sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer): (String) -> Produktkode {
    return { produktKode: String ->
        if (!sjekkProduktKodeEksisterer(produktKode)) { // Kall eksterne tjeneste
            throw UgyldigOrdreException("Ugyldig kode: Produktet '$produktKode' eksisterer ikke")
        }
        Produktkode(produktKode)
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
    val ordreMengde: Mengde,
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
    val prisedeOrdrelinjer = validertBestilling.ordrelinjer.map { prisOrdreLinje(getProduktPris, it) }
    val fakturaSum = prisedeOrdrelinjer.sumOf { it.linjePris.value.toDouble() }
    return Ok(
        PrisetBestilling(
            ordreId = validertBestilling.ordreId,
            fakturaadresse = validertBestilling.fakturaAdresse,
            kundeInfo = validertBestilling.kundeInfo,
            leveringsadresse = validertBestilling.leveringsadresse,
            priseteOrdrelinjer = prisedeOrdrelinjer,
            fakturaSum = Pris.of(fakturaSum)
        )
    )
}

private fun prisOrdreLinje(getProduktPris: HentProduktPris, ordrelinje: ValidertOrdrelinje): PrisetOrdrelinje {
    return getProduktPris(ordrelinje.produktkode)
        .let { ordrelinje.ordreMengde.value * it }
        .let { Pris.of(it) }
        .let { PrisetOrdrelinje(it) }
}

// ==================================
// Bekreft bestilling steg
// ==================================
private fun bekreftBestilling(
    lagBekreftelsesEpostHtml: LagBekreftelsesEpostHtml,  // Dependency
    sendBekreftelsesEpost: SendBekreftelsesEpost,        // Dependency
    prisetBestilling: PrisetBestilling                   // Input
): Result<BekreftetBestilling, Valideringsfeil> {
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
): List<PlasserBestillingHendelse> {
    val fakturerbarHendelse = PlasserBestillingHendelse.FakturaHendelse(
        ordreId = prisetBestilling.ordreId,
        fakturadresse = prisetBestilling.fakturaadresse,
        fakturasum = prisetBestilling.fakturaSum
    )
    return listOf(
        PlasserBestillingHendelse.BestillingAkseptertHendelse(prisetBestilling),
        fakturerbarHendelse
    ) + when (sendEpostResultat) {
        SendEpostResultat.Sendt -> listOf(
            PlasserBestillingHendelse.BekreftelseSentTilBrukerHendelse(prisetBestilling.kundeInfo.kundeEpost)
        )
        SendEpostResultat.Ikke_sendt -> emptyList()
    }
}
