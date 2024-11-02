package PlasserBestillingWorkflow

import com.github.michaelbull.result.*
import utils.NonEmptyList
import java.lang.RuntimeException

// Eksempelimplementasjon av PlasserBestilling

val plasserBestilling: PlasserBestilling = { bestilling: Bestilling ->
    validerBestilling(
        sjekkProduktKodeEksisterer,
        sjekkAdresseEksisterer,
        bestilling.bestilling
    )
        .andThen {
            Ok(
                BestillingPlassertHendelser(
                    bekreftelseSent = true,
                    ordrePlassert = true,
                    fakturerbarOrdrePlassert = true
                )
            )
        }
}

private val validerBestilling: ValiderBestilling =
    { sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer, // Dependency
      sjekkAdresseEksisterer: SjekkAdresseEksisterer, // Dependency
      bestilling: IkkeValidertBestilling -> // Input

        val tilProduktKode = { produktKode: String ->
            if (sjekkProduktKodeEksisterer(produktKode)) {
                throw RuntimeException("Ugyldig kode") // TODO Replace with result type
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
                throw RuntimeException("Ugyldig adresse")
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
val sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer = { produktKode -> produktKode == "VALID_CODE" }
val sjekkAdresseEksisterer: SjekkAdresseEksisterer = { adresse -> adresse.isNotEmpty() }
