package PlasserBestillingWorkflow

import com.github.michaelbull.result.*
import utils.NonEmptyList
import java.lang.RuntimeException

// Eksempelimplementasjon av PlasserBestillingWorkflow

val plasserBestillingWorkflow: PlasserBestillingWorkflow = { bestilling: Bestilling ->
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

typealias PlasserBestillingWorkflowSetup = (SjekkProduktKodeEksisterer, SjekkAdresseEksisterer) -> PlasserBestillingWorkflow

val plasserBestillingWorkflowSetup: PlasserBestillingWorkflowSetup =
    { sjekkProduktKodeEksisterer, sjekkAdresseEksisterer ->
        plasserBestillingWorkflow
    }

private val validerBestilling: ValiderBestilling =
    { sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer, // Dependency
      sjekkAdresseEksisterer: SjekkAdresseEksisterer, // Dependency
      bestilling: IkkeValidertBestilling -> // Input

        val tilProduktKode = { produktKode: String ->
            if (!sjekkProduktKodeEksisterer(produktKode)) {
                throw UgyldigOrdreException("Ugyldig kode") // TODO Replace with result type
            }
            Produktkode.Klatreutstyr(KlatreutstyrKode(produktKode))
        }
        val tilValidertOrdrelinje = { ordrelinje: IkkeValidertBestilling.IkkeValidertOrdrelinje ->
            Ordrelinje(
                id = "",
                ordreId = "",
                produktkode = tilProduktKode(ordrelinje.produktkode),
                ordreMengde = OrdreMengde.Enhet(Enhetsmengde(2)),
                pris = ""
            )
        }
        val tilValidertAdresse = { adresselinje: String ->
            if (sjekkAdresseEksisterer(adresselinje)) {
                ValidertAdresse(adresselinje)
            } else {
                throw UgyldigOrdreException("Ugyldig adresse")
            }

        }

        Ok(
            ValidertBestilling(
                id = bestilling.ordreId,
                fakturaAdresse = "placeholder",
                kundeId = "placeholder",
                leveringsadresse = tilValidertAdresse(bestilling.leveringsadresse),
                ordrelinjer = NonEmptyList.fromList(bestilling.ordrelinjer.map { tilValidertOrdrelinje(it) }),
                sumSomSkalBliBelastet = "Nothing"
            )
        )
    }


// Hjelpefunksjoner
val sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer = { produktKode ->
    val gyldigeProdukter = setOf("MagDust", "Magnus Tskjorte")
    produktKode in gyldigeProdukter
}
val sjekkAdresseEksisterer: SjekkAdresseEksisterer = { adresse -> adresse.isNotEmpty() }
