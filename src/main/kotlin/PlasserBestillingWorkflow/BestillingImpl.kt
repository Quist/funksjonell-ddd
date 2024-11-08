package PlasserBestillingWorkflow

import PlasserBestillingWorkflow.IkkeValidertBestilling.IkkeValidertOrdrelinje
import com.github.michaelbull.result.*
import utils.NonEmptyList

// Eksempelimplementasjon av PlasserBestillingWorkflow
fun initializePlasserBestillingWorkflow(
    sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer, // Dependency
    sjekkAdresseEksisterer: SjekkAdresseEksisterer // Dependency
): PlasserBestillingWorkflow {
    return fun(bestilling: Bestilling): Result<BestillingPlassertHendelser, Nothing> {
        return validerBestilling(
            sjekkProduktKodeEksisterer,
            sjekkAdresseEksisterer,
            bestilling.bestilling
        ).andThen {
            Ok(
                BestillingPlassertHendelser(
                    bekreftelseSent = true,
                    ordrePlassert = true,
                    fakturerbarOrdrePlassert = true
                )
            )
        }
    }
}

// Valider Bestilling steg
private fun validerBestilling(
    sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer, // Dependency
    sjekkAdresseEksisterer: SjekkAdresseEksisterer, // Dependency
    bestilling: IkkeValidertBestilling // Input
): Result<ValidertBestilling, Nothing> {
    val ordreId = OrdreId.of(bestilling.ordreId)
    val leveringsadresse = tilValidertAdresse(sjekkAdresseEksisterer, bestilling.leveringsadresse)
    val fakturadresse = tilValidertAdresse(sjekkAdresseEksisterer, bestilling.fakturadresse)
    val ordrelinjer = tilValiderteOrdrelinjer(tilProduktKode(sjekkProduktKodeEksisterer), bestilling.ordrelinjer)
    val kundeInfo = KundeInfo(KundeId.of(bestilling.kundeinfo))

    return Ok(
        ValidertBestilling(
            ordreId = ordreId,
            fakturaAdresse = fakturadresse,
            leveringsadresse = leveringsadresse,
            kundeInfo = kundeInfo,
            ordrelinjer = ordrelinjer,
            sumSomSkalBliBelastet = "Nothing"
        )
    )
}

private fun tilValiderteOrdrelinjer(
    tilProduktKode: (String) -> Produktkode,
    ordrelinjer: List<IkkeValidertOrdrelinje>
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
        pris = ""
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
    adresselinje: String
): ValidertAdresse {
    return if (sjekkAdresseEksisterer(adresselinje)) { // Kall eksterne tjeneste
        ValidertAdresse(adresselinje)
    } else {
        throw UgyldigAdresse("Ugyldig adresse: $adresselinje")
    }
}

data class ValidertBestilling(
    private val ordreId: OrdreId,
    private val kundeInfo: KundeInfo,
    private val leveringsadresse: ValidertAdresse,
    private val fakturaAdresse: ValidertAdresse,
    private val ordrelinjer: NonEmptyList<ValidertOrdrelinje>, // TODO Endre tilbake til vanlig list for å få testen til å feile.
    private val sumSomSkalBliBelastet: FakturaSum
)

data class ValidertOrdrelinje(
    private val produktkode: Produktkode,
    private val ordreMengde: OrdreMengde,
    private val pris: Pris
)

data class ValidertAdresse(val adresselinje: String)
data class KundeInfo(val kundeId: KundeId) // TODO Vurder å introdusere konseptet med validerte eposter

// Sub-workflows TODO: Plasser der de hører hjemme?
typealias PrisOrdre = (HentProduktPris) -> (ValidertBestilling) -> PrisetBestilling

// Hjelpefunksjoner (typisk services i objekt-orienterte språk)
typealias SjekkProduktKodeEksisterer = (String) -> Boolean
typealias SjekkAdresseEksisterer = (String) -> Boolean
typealias HentProduktPris = () -> Nothing