package PlasserBestillingWorkflow

import PlasserBestillingWorkflow.IkkeValidertBestilling.IkkeValidertOrdrelinje
import com.github.michaelbull.result.*
import utils.NonEmptyList


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
            .map { bekreftBestilling(lagBekreftelsesEpostHtml, sendBekreftelsesEpost, it) }
            .flatMap { pair -> Ok(lagHendelser(pair.second, pair.first)) }
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
): NonEmptyList<ValidertOrdrelinje> {
    return NonEmptyList.fromList(ordrelinjer.map {
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

    return if (sjekkAdresseEksisterer(adresse)) { // Kall ekstern tjeneste
        ValidertAdresse(adresse.gateadresse, adresse.postnummer.toInt())
    } else {
        throw UgyldigAdresse("Ugyldig adresse: $adresse")
    }
}

data class ValidertBestilling(
    val ordreId: OrdreId,
    val kundeInfo: KundeInfo,
    val leveringsadresse: ValidertAdresse,
    val fakturaAdresse: ValidertAdresse,
    val ordrelinjer: NonEmptyList<ValidertOrdrelinje>, // TODO Endre tilbake til vanlig list for å få testen til å feile.
)

data class ValidertOrdrelinje(
    val produktkode: Produktkode,
    val ordreMengde: OrdreMengde,
)

data class ValidertAdresse(val gateadresse: String, val postnummer: Number)
data class KundeInfo(val kundeId: KundeId, val kundeEpost: String) // TODO Vurder å introdusere konseptet med validerte eposter

// ==================================
// Pris bestilling steg
// ==================================
private fun prisOrdre(
    getProduktPris: HentProduktPris, validertBestilling: ValidertBestilling
): Result<PrisetBestilling, String> {
    val faktureringssum =
        validertBestilling.ordrelinjer.items.map { prisOrdreLinje(getProduktPris, it) }.sumOf { it.toDouble() }
            .let { Pris.of(it) }
    return Ok(
        PrisetBestilling(
            ordreId = validertBestilling.ordreId,
            fakturaadresse = validertBestilling.fakturaAdresse,
            kundeInfo = validertBestilling.kundeInfo,
            leveringsadresse = validertBestilling.leveringsadresse,
            faktureringssum = faktureringssum
        )
    )
}

private fun prisOrdreLinje(getProduktPris: HentProduktPris, ordrelinje: ValidertOrdrelinje): Number {
    val quantity: Int = when (ordrelinje.ordreMengde) { // TODO Consider this structure
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
): Pair<SendEpostResultat, PrisetBestilling> {
    lagBekreftelsesEpostHtml(prisetBestilling).let { letter ->
        return Pair(sendBekreftelsesEpost(prisetBestilling.kundeInfo.kundeEpost, letter), prisetBestilling)
    }
}

// ==================================
// Lag hendelser
// ==================================
private fun lagHendelser(prisetBestilling: PrisetBestilling, sendEpostResultat: SendEpostResultat): PlasserBestillingHendelser {
    return PlasserBestillingHendelser(
        bekreftelseSent = sendEpostResultat, ordrePlassert = prisetBestilling, fakturerbarOrdrePlassert = lagFakturerBarHendelse(prisetBestilling)
    )
}

private fun lagFakturerBarHendelse(prisetBestilling: PrisetBestilling): FakturerbarOrdrePlassert? {
    return if (prisetBestilling.faktureringssum.value.toDouble() > 0) {
        FakturerbarOrdrePlassert(ordreId = prisetBestilling.ordreId, fakturadresse = prisetBestilling.fakturaadresse, fakturasum = prisetBestilling.faktureringssum)
    } else null
}