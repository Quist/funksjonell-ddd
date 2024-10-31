import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class Oppgaver {

    @Test
    fun oppgave2a() {
        // Test som lager en ordre med ulovelig Enhetsmengde
        assertThrows<IllegalStateException> {  Kilogrammengde.of(-1F) }
        assertThrows<IllegalStateException> {  Enhetsmengde(10000) }
    }

    @Test
    fun oppgave2b() {
        assertThrows<UgyldigOrdreException> {
            plasserBestilling(
                IkkeValidertBestilling(
                    ordreId = "1",
                    kundeinfo = "Adam Åndra",
                    leveringsadresse = IkkeValidertBestilling.UvalidertAdresse("Gateavisa 1"),
                    ordrelinjer = emptyList() // Ordrelinjer kan ikke vært tom.
                )
            )
        }
    }
}