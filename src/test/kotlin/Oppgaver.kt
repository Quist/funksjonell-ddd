import PlasserBestillingWorkflow.*
import PlasserBestillingWorkflow.IkkeValidertBestilling.IkkeValidertOrdrelinje
import com.github.michaelbull.result.mapBoth
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.junit5.JUnit5Asserter.fail

class Oppgaver {

    // Initialize plasserBestilling med dependencies og eventuelt test data.
    // Funksjonell måte å gjøre dependency injection på.
    val plasserBestillingWorkflow: PlasserBestillingWorkflow =
        plasserBestillingWorkflowSetup(sjekkProduktKodeEksisterer, sjekkAdresseEksisterer)

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
    @DisplayName("Invariant for enhetsmengde")
    fun oppgave2a() {
        // Test som lager en ordre med ulovlig Enhetsmengde
        // TODO Denne skal feile
        plasserBestillingWorkflow(
            eksempelBestilling.copy(
                bestilling = eksempelBestilling.bestilling.copy(
                    ordrelinjer = listOf(
                        IkkeValidertOrdrelinje(
                            produktkode = "MagDust",
                            mengde = 10_000
                        )
                    )
                )
            )
        )
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
}

private val eksempelBestilling = Bestilling(
    bestilling = IkkeValidertBestilling(
        ordreId = "1",
        kundeinfo = "Adam Åndra",
        leveringsadresse = "Testveien 5",
        fakturadresse = "Testveien 5",
        ordrelinjer = listOf(IkkeValidertOrdrelinje("MagDust", mengde = 10_000))
    ),
    time = LocalDateTime.now(),
    userId = "123"
)