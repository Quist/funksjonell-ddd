import PlasserBestillingWorkflow.*
import PlasserBestillingWorkflow.IkkeValidertBestilling.IkkeValidertOrdrelinje
import com.github.michaelbull.result.getOrThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.DefaultAsserter.assertNull

class Oppgaver {

    // Her konstuerer vi "hovedfunksjonen" ved å pakke inn all dependencies som trengs.
    val plasserBestillingWorkflow: PlasserBestillingWorkflow = initializePlasserBestillingWorkflow(
        ::sjekkProduktKodeEksisterer,
        ::sjekkAdresseEksisterer,
        ::hentProduktPris,
        ::lagBekreftelsesEpostHtml,
        ::sendBekreftelsesEpost,
        ::sjekkEpostStatus
    )

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
                leveringsadresse = IkkeValidertBestilling.IkkeValidertAdresse("Bryggen", "-55")
            )
        )
        assertThrows<UgyldigAdresse> { plasserBestillingWorkflow(bestilling) }
    }

    @Test
    @DisplayName("Oppgave 1c: Valider at adressen finnes")
    fun oppgave1c() {
        val bestillingMedUkjentAdresse = eksempelGyldigBestilling.copy(
            bestilling = eksempelGyldigBestilling.bestilling.copy(
                leveringsadresse = IkkeValidertBestilling.IkkeValidertAdresse("Ukjent Adresse", "5211")
            )
        )
        assertDoesNotThrow { plasserBestillingWorkflow(eksempelGyldigBestilling) }
        assertThrows<UkjentAdresse> { plasserBestillingWorkflow(bestillingMedUkjentAdresse) }
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

        val bekreftelseSendtHendelse = plasserBestillingWorkflow(bestilling)
            .getOrThrow({throw IllegalStateException()}) // Unwraps the result, throwing if it's an error
            .find { it is PlasserBestillingHendelse.BekreftelseSentTilBrukerHendelse }
        assertNull("Forventet at det ikke skulle sendes en bekreftelse til bruker", bekreftelseSendtHendelse)
    }

    @Test
    @DisplayName("Oppgave 3a: Ordrelinjer kan ikke være en tom liste")
    fun oppgave3a() {
        val bestilling = eksempelGyldigBestilling.copy(
            bestilling = eksempelGyldigBestilling.bestilling.copy(
                ordrelinjer = emptyList()
            )
        )
        assertThrows<UgyldigeOrdreLinjer> { plasserBestillingWorkflow(bestilling) }
    }

    @Test
    @DisplayName("Oppgave 4a: Workflow Hendelser")
    fun oppgave4a() {
        val result = plasserBestillingWorkflow(eksempelGyldigBestilling)
        if (result.isErr) {
            throw IllegalStateException("Forventet at resultatet skulle være Ok.")
        }
        throw NotImplementedError("Ikke implementert: Sjekk at det returneres en BekreftelseSentTilBrukerHendelse")
    }

    @Test
    @DisplayName("Oppgave 4b: FakturaEvent blir ikke generert når summen er 0")
    fun oppgave4b() {
        val bestilling = eksempelGyldigBestilling.copy(
            bestilling = eksempelGyldigBestilling.bestilling.copy(
                ordrelinjer = listOf(IkkeValidertOrdrelinje(gratisProdukt, "50"))
            )
        )

        val fakturaHendelse = plasserBestillingWorkflow(bestilling)
            .getOrThrow({throw IllegalStateException()}) // Unwraps the result, throwing if it's an error
            .find { it is PlasserBestillingHendelse.FakturaHendelse } as? PlasserBestillingHendelse.FakturaHendelse

        assertNull("Forventet ikke BestillingPlassert-hendelse", fakturaHendelse)
    }

}

private val eksempelGyldigBestilling = Bestilling(
    bestilling = IkkeValidertBestilling(
        ordreId = "1",
        kundeId = "Adam Åndra",
        kundeEpost = "adam.ondra@climbing.com",
        leveringsadresse = IkkeValidertBestilling.IkkeValidertAdresse("Testveien 7", "2070"),
        fakturadresse = IkkeValidertBestilling.IkkeValidertAdresse("Testveien 7", "2070"),
        ordrelinjer = listOf(IkkeValidertOrdrelinje("MagDust", mengde = "10000"))
    ),
    time = LocalDateTime.now(),
)