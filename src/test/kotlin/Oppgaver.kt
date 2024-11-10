import PlasserBestillingWorkflow.*
import PlasserBestillingWorkflow.IkkeValidertBestilling.IkkeValidertOrdrelinje
import com.github.michaelbull.result.mapBoth
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.junit5.JUnit5Asserter.fail

class Oppgaver {

    // Her konstuerer vi "hovedfunksjonen" ved å pakke inn all dependencies som trengs.
    val plasserBestillingWorkflow: PlasserBestillingWorkflow = initializePlasserBestillingWorkflow(
        ::sjekkProduktKodeEksisterer,
        ::sjekkAdresseEksisterer,
        ::hentProduktPris,
        ::lagBekreftelsesEpostHtml,
        ::sendBekreftelsesEpost
    )

    @Nested
    @DisplayName("Del 1 - Validering")
    inner class Del1 {

        @Test
        @DisplayName("Oppgave 1a: Gjør det umulig å lage ValidertAdresse uten gateadresse")
        fun oppgave1a() {
            assertThrows<UgyldigAdresse> {
                ValidertAdresse("", 0)
            }
        }

        @Test
        @DisplayName("Oppgave 1b: Gjør det umulig å sende inn en bestilling uten et gyldig postnummer")
        fun oppgave1b() {
            val bestilling = eksempelGyldigBestilling.copy(
                bestilling = eksempelGyldigBestilling.bestilling.copy(
                    leveringsadresse = IkkeValidertBestilling.IkkeValidertAdresse("Testveien 7", "-55")
                )
            )
            assertThrows<UgyldigAdresse> { plasserBestillingWorkflow(bestilling) }
        }

        @Test
        @DisplayName("Oppgave 1c: Gjør det umulig å sende inn en ukjent adresse")
        fun oppgave1c() {
            val bestilling = eksempelGyldigBestilling.copy(
                bestilling = eksempelGyldigBestilling.bestilling.copy(
                    leveringsadresse = IkkeValidertBestilling.IkkeValidertAdresse("Ukjent Adresse", "5211")
                )
            )
            assertThrows<UkjentAdresse> { plasserBestillingWorkflow(bestilling) }
        }

        @Test
        @DisplayName("Oppgave 2a: Valider epost")
        fun oppgave2a() {
            val bestilling = eksempelGyldigBestilling.copy(
                bestilling = eksempelGyldigBestilling.bestilling.copy(
                    kundeEpost = "testeposten"
                )
            )
            assertThrows<UgyldigEpost> { plasserBestillingWorkflow(bestilling) }
        }

        @Test
        @DisplayName("Oppgave 2b: Sjekk verifisert status")
        fun oppgave2b() {
            val bestilling = eksempelGyldigBestilling.copy(
                bestilling = eksempelGyldigBestilling.bestilling.copy(
                    kundeEpost = "ikke_verifisert_epost@hotmail.com"
                )
            )
            assertThrows<EpostIkkeVerifisert> { plasserBestillingWorkflow(bestilling) }
        }

    }


    @Test
    @DisplayName("Invariant for ordrelinjer")
    fun oppgave2b() {
        assertThrows<IllegalStateException> {
            plasserBestillingWorkflow(
                eksempelGyldigBestilling.copy(
                    bestilling = eksempelGyldigBestilling.bestilling.copy(ordrelinjer = emptyList())
                )
            )
        }
    }

    @Test
    // TODO Implementer
    @DisplayName("Oppgave der man fikser en bug med valider adresse som ikke har privat konstruktør.")
    fun adresseNonPrivatKonstrtør() {
        assertThrows<IllegalStateException> {
            plasserBestillingWorkflow(
                eksempelGyldigBestilling.copy(
                    bestilling = eksempelGyldigBestilling.bestilling.copy(ordrelinjer = emptyList())
                )
            )
        }
    }

    @Test
    @DisplayName("Validering av produktet faktisk finnes")
    fun oppgave2c() {
        assertThrows<UgyldigOrdreException> {
            plasserBestillingWorkflow(
                eksempelGyldigBestilling.copy(
                    bestilling = eksempelGyldigBestilling.bestilling.copy(
                        ordrelinjer = listOf(
                            IkkeValidertOrdrelinje(
                                produktkode = "Finnes ikke",
                                mengde = 10_000
                            )
                        )
                    )
                )
            )
        }
    }

    @Nested
    @DisplayName("Implementasjons tester - ikke en del av oppgavene")
    inner class Tester {
        @Test
        fun happyCase() {
            val result = plasserBestillingWorkflow(eksempelGyldigBestilling)
            result.mapBoth(
                success = { value -> assertTrue(true) },
                failure = { error -> fail("Expected the result to be success, but instead it was: " + result.error) }
            )
        }

        @Test
        @DisplayName("OrdreId kan ikke være tom")
        fun ordreIdValideres() {
            assertThrows<UgyldigOrdreException> {
                plasserBestillingWorkflow(
                    eksempelGyldigBestilling.copy(
                        bestilling = eksempelGyldigBestilling.bestilling.copy(ordreId = "")
                    )
                )
            }
        }


        @Test
        @DisplayName("Ordren skal prises")
        fun ordrePrises() {
            val result = plasserBestillingWorkflow(eksempelGyldigBestilling)
            result.mapBoth(
                success = { value -> assertEquals(50_000, value.fakturerbarOrdrePlassert?.fakturasum?.value) },
                failure = { error -> fail("Expected the result to be success, but instead it was: " + result.error) }
            )
        }

    }
}

private val eksempelGyldigBestilling = Bestilling(
    bestilling = IkkeValidertBestilling(
        ordreId = "1",
        kundeId = "Adam Åndra",
        kundeEpost = "test@testesen.com",
        leveringsadresse = IkkeValidertBestilling.IkkeValidertAdresse("Testveien 7", "2070"),
        fakturadresse = IkkeValidertBestilling.IkkeValidertAdresse("Testveien 7", "2070"),
        ordrelinjer = listOf(IkkeValidertOrdrelinje("MagDust", mengde = 10_000))
    ),
    time = LocalDateTime.now(),
)