import PlasserBestillingWorkflow.*
import PlasserBestillingWorkflow.IkkeValidertBestilling.IkkeValidertOrdrelinje
import com.github.michaelbull.result.mapBoth
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.junit5.JUnit5Asserter.fail

class Oppgaver {

    // Initialize plasserBestilling med dependencies og eventuelt test data.
    // Funksjonell måte å gjøre dependency injection på.
    val plasserBestillingWorkflow: PlasserBestillingWorkflow = initializePlasserBestillingWorkflow(
        ::sjekkProduktKodeEksisterer,
        ::sjekkAdresseEksisterer,
        ::hentProduktPris,
        ::lagBekreftelsesEpostHtml,
        ::sendBekreftelsesEpost
    )

    @Test
    @Ignore
    @DisplayName("Ugyldige enhetsmengder valideres")
    fun oppgave2a() {
        // Test som lager en ordre med ulovlig Enhetsmengde
        val bestilling = eksempelBestilling.bestilling.copy(
            ordrelinjer = listOf(
                IkkeValidertOrdrelinje(
                    produktkode = "MagDust",
                    mengde = 10_000
                )
            )
        )
        assertThrows<UgyldigAdresse> {
            plasserBestillingWorkflow(
                eksempelBestilling.copy(bestilling = bestilling)
            )
        }
    }

    @Test
    @DisplayName("Invariant for ordrelinjer")
    fun oppgave2b() {
        assertThrows<IllegalStateException> {
            plasserBestillingWorkflow(
                eksempelBestilling.copy(
                    bestilling = eksempelBestilling.bestilling.copy(ordrelinjer = emptyList())
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
                eksempelBestilling.copy(
                    bestilling = eksempelBestilling.bestilling.copy(ordrelinjer = emptyList())
                )
            )
        }
    }

    @Test
    @DisplayName("Validering av produktet faktisk finnes")
    fun oppgave2c() {
        assertThrows<UgyldigOrdreException> {
            plasserBestillingWorkflow(
                eksempelBestilling.copy(
                    bestilling = eksempelBestilling.bestilling.copy(
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
            val result = plasserBestillingWorkflow(eksempelBestilling)
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
                    eksempelBestilling.copy(
                        bestilling = eksempelBestilling.bestilling.copy(ordreId = "")
                    )
                )
            }
        }

        @Test
        @DisplayName("Adresser skal valideres")
        fun adresserIOrdreValideres() {
            assertThrows<UgyldigAdresse> {
                plasserBestillingWorkflow(
                    eksempelBestilling.copy(
                        bestilling = eksempelBestilling.bestilling.copy(leveringsadresse = "")
                    )
                )
            }

            assertThrows<UgyldigAdresse> {
                plasserBestillingWorkflow(
                    eksempelBestilling.copy(
                        bestilling = eksempelBestilling.bestilling.copy(fakturadresse = "")
                    )
                )
            }
        }

        @Test
        @DisplayName("Ordren skal prises")
        fun ordrePrises() {
            val result = plasserBestillingWorkflow(eksempelBestilling)
            result.mapBoth(
                success = { value -> assertEquals(50_000, value.fakturerbarOrdrePlassert?.fakturasum?.value) },
                failure = { error -> fail("Expected the result to be success, but instead it was: " + result.error) }
            )
        }

    }
}

private val eksempelBestilling = Bestilling(
    bestilling = IkkeValidertBestilling(
        ordreId = "1",
        kundeId = "Adam Åndra",
        kundeEpost = "test@testesen.com",
        leveringsadresse = "Testveien 5",
        fakturadresse = "Testveien 5",
        ordrelinjer = listOf(IkkeValidertOrdrelinje("MagDust", mengde = 10_000))
    ),
    time = LocalDateTime.now(),
)