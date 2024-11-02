import PlasserBestillingWorkflow.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertTrue

class Oppgaver {

    @Test
    @DisplayName("Invariant for enhetsmengde")
    fun oppgave2a() {
        // Test som lager en ordre med ulovelig Enhetsmengde
        assertThrows<IllegalStateException> { Kilogrammengde.of(-1F) }
        assertThrows<IllegalStateException> { Enhetsmengde(10000) }
    }

    @Test
    @DisplayName("Invariant for ordrelinjer")
    fun oppgave2b() {
        assertThrows<UgyldigOrdreException> {
            plasserBestilling(
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
            val result = plasserBestilling(
                Bestilling(
                    bestilling = IkkeValidertBestilling(
                        ordreId = "1",
                        kundeinfo = "Adam Åndra",
                        leveringsadresse = "Eidsvollveien",
                        ordrelinjer = listOf(IkkeValidertBestilling.IkkeValidertOrdrelinje("Finnes ikke"))
                    ),
                    time = LocalDateTime.now(),
                    userId = "123"
                )
            )

        assertTrue(result.isErr, "Forventet at bestillingen skulle feile siden ordren ikke var tilstede. I steden var resultatet: " + result.value)
    }
}