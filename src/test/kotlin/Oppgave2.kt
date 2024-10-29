import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class Oppgave2 {

    @Test
    fun oppgave2b() {
        // Test som lager en ordre med ulovelig Enhetsmengde
        assertThrows<IllegalStateException> {  Kilogrammengde.of(-1F) }
        assertThrows<IllegalStateException> {  Enhetsmengde(10000) }
    }
}