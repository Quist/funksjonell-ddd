import PlasserBestillingWorkflow.*
import PlasserBestillingWorkflow.IkkeValidertBestilling.IkkeValidertOrdrelinje
import com.github.michaelbull.result.mapBoth
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.fail

class InternalTests {
    // Her konstuerer vi "hovedfunksjonen" ved å pakke inn all dependencies som trengs.
    val plasserBestillingWorkflow: PlasserBestillingWorkflow = initializePlasserBestillingWorkflow(
        ::sjekkProduktKodeEksisterer,
        ::sjekkAdresseEksisterer,
        ::hentProduktPris,
        ::lagBekreftelsesEpostHtml,
        ::sendBekreftelsesEpost
    )

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
            success = { value -> assertEquals(50_000.0, value.fakturerbarOrdrePlassert?.fakturasum?.value) },
            failure = { error -> fail("Expected the result to be success, but instead it was: " + result.error) }
        )
    }
}

private val eksempelGyldigBestilling = Bestilling(
    bestilling = IkkeValidertBestilling(
        ordreId = "1",
        kundeId = "Adam Åndra",
        kundeEpost = "test@testesen.com",
        leveringsadresse = IkkeValidertBestilling.IkkeValidertAdresse("Testveien 7", "2070"),
        fakturadresse = IkkeValidertBestilling.IkkeValidertAdresse("Testveien 7", "2070"),
        ordrelinjer = listOf(IkkeValidertOrdrelinje("MagDust", mengde = "10000"))
    ),
    time = LocalDateTime.now(),
)
