import PlasserBestillingWorkflow.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class Oppgaver {

    // Initialize plasserBestilling med dependencies og eventuelt test data.
    // Funksjonell måte å gjøre dependency injection på.
    val plasserBestillingWorkflow: PlasserBestillingWorkflow = plasserBestillingWorkflowSetup(sjekkProduktKodeEksisterer, sjekkAdresseEksisterer)

    @Test
    fun happyCase() {
        assertThrows<UgyldigOrdreException> {
            plasserBestillingWorkflow(
                Bestilling(
                    bestilling = IkkeValidertBestilling(
                        ordreId = "1",
                        kundeinfo = "Adam Åndra",
                        leveringsadresse = "",
                        ordrelinjer = listOf(IkkeValidertBestilling.IkkeValidertOrdrelinje("MagDust", mengde = 10_000))
                    ),
                    time = LocalDateTime.now(),
                    userId = "123"
                )
            )
        }
        /**
        result.mapBoth(
            success = { value -> assertTrue(true) },
            failure = { error -> fail("Expected the result to be success, but instead it was: " + result.error) }
        )
        */
    }

    @Test
    @DisplayName("Invariant for enhetsmengde")
    fun oppgave2a() {
        // Test som lager en ordre med ulovelig Enhetsmengde
        plasserBestillingWorkflow(
            Bestilling(
                bestilling = IkkeValidertBestilling(
                    ordreId = "1",
                    kundeinfo = "Adam Åndra",
                    leveringsadresse = "Testveien 5",
                    ordrelinjer = listOf(IkkeValidertBestilling.IkkeValidertOrdrelinje(produktkode = "MagDust", mengde = 10_000))
                ),
                time = LocalDateTime.now(),
                userId = "123"
            )
        )
    }

    @Test
    @DisplayName("Invariant for ordrelinjer")
    fun oppgave2b() {
        assertThrows<UgyldigOrdreException> {
            plasserBestillingWorkflow(
                Bestilling(
                    bestilling = IkkeValidertBestilling(
                        ordreId = "1",
                        kundeinfo = "Adam Åndra",
                        leveringsadresse = "",
                        ordrelinjer = emptyList() // Ordrelinjer kan ikke vært tom.
                    ),
                    time = LocalDateTime.now(),
                    userId = "123"
                )
            )
        }
    }

    @Test
    @DisplayName("Validering av produktet faktisk finnes")
    fun oppgave2c() {
        assertThrows<UgyldigOrdreException> {
            plasserBestillingWorkflow(
                Bestilling(
                    bestilling = IkkeValidertBestilling(
                        ordreId = "1",
                        kundeinfo = "Adam Åndra",
                        leveringsadresse = "Eidsvollveien",
                        ordrelinjer = listOf(IkkeValidertBestilling.IkkeValidertOrdrelinje(produktkode = "Finnes ikke", mengde = 10_000))
                    ),
                    time = LocalDateTime.now(),
                    userId = "123"
                )
            )
        }
    }
}