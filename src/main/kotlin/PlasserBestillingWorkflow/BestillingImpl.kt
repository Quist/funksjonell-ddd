package PlasserBestillingWorkflow

import com.github.michaelbull.result.*
import utils.NonEmptyList

// Eksempelimplementasjon av PlasserBestillingWorkflow

typealias PlasserBestillingWorkflowSetup = (SjekkProduktKodeEksisterer, SjekkAdresseEksisterer) -> PlasserBestillingWorkflow
val plasserBestillingWorkflowSetup: PlasserBestillingWorkflowSetup =
    { sjekkProduktKodeEksisterer, sjekkAdresseEksisterer ->
        { bestilling: Bestilling ->
            validerBestilling(
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
typealias ValiderBestilling = (SjekkProduktKodeEksisterer, SjekkAdresseEksisterer, IkkeValidertBestilling) -> Result<ValidertBestilling, Valideringsfeil>
private val validerBestilling: ValiderBestilling =
    { sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer, // Dependency
      sjekkAdresseEksisterer: SjekkAdresseEksisterer, // Dependency
      bestilling: IkkeValidertBestilling -> // Input
        Ok(
            ValidertBestilling(
                ordreId = OrdreId.of(bestilling.ordreId),
                fakturaAdresse = "placeholder",
                kundeId = "placeholder",
                leveringsadresse = tilValidertAdresse(sjekkAdresseEksisterer, bestilling.leveringsadresse),
                ordrelinjer = NonEmptyList.fromList(bestilling.ordrelinjer.map {
                    tilValidertValidertOrdrelinje(
                        tilProduktKode(
                            sjekkProduktKodeEksisterer
                        ), it
                    )
                }),
                sumSomSkalBliBelastet = "Nothing"
            )
        )
    }

private val tilValidertValidertOrdrelinje =
    { tilProduktKode: (String) -> Produktkode, ordrelinje: IkkeValidertBestilling.IkkeValidertOrdrelinje ->
        ValidertOrdrelinje(
            produktkode = tilProduktKode(ordrelinje.produktkode),
            ordreMengde = OrdreMengde.Enhet(Enhetsmengde(2)),
            pris = ""
        )
    }

private val tilProduktKode = { sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer ->
    { produktKode: String ->
        if (!sjekkProduktKodeEksisterer(produktKode)) {
            throw UgyldigOrdreException("Ugyldig kode") // TODO Replace with result type
        }
        Produktkode.Klatreutstyr(KlatreutstyrKode(produktKode))
    }
}

private val tilValidertAdresse = { sjekkAdresseEksisterer: SjekkAdresseEksisterer, adresselinje: String ->
    if (sjekkAdresseEksisterer(adresselinje)) { // Kall den eksterne tjenesten
        ValidertAdresse(adresselinje)
    } else {
        throw UgyldigOrdreException("Ugyldig adresse")
    }
}

data class ValidertBestilling(
    private val ordreId: OrdreId,
    private val kundeId: KundeId,
    private val leveringsadresse: ValidertAdresse,
    private val fakturaAdresse: FakturaAdresse,
    private val ordrelinjer: NonEmptyList<ValidertOrdrelinje>, // TODO Endre tilbake til vanlig list for å få testen til å feile.
    private val sumSomSkalBliBelastet: FakturaSum
)
data class ValidertOrdrelinje(
    private val produktkode: Produktkode,
    private val ordreMengde: OrdreMengde,
    private val pris: Pris
)

data class ValidertAdresse(val adresselinje: String)

// Sub-workflows TODO: Plasser der de hører hjemme?
typealias PrisOrdre = (HentProduktPris) -> (ValidertBestilling) -> PrisetBestilling

// Hjelpefunksjoner (typisk services i objekt-orienterte språk)
typealias SjekkProduktKodeEksisterer = (String) -> Boolean
typealias SjekkAdresseEksisterer = (String) -> Boolean
typealias HentProduktPris = () -> Nothing