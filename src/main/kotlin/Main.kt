import PlasserBestillingWorkflow.*
import java.time.LocalDateTime

fun main(args: Array<String>) {

    val plasserBestillingWorkflow: PlasserBestillingWorkflow =
        initializePlasserBestillingWorkflow(::sjekkProduktKodeEksisterer, ::sjekkAdresseEksisterer, ::hentProduktPris, ::lagBekreftelsesEpostHtml, ::sendBekreftelsesEpost)

    plasserBestillingWorkflow(
        Bestilling(
            IkkeValidertBestilling(
                fakturadresse = IkkeValidertBestilling.IkkeValidertAdresse("Testveien 7", "2070"),
                ordreId = "12",
                kundeId = "123",
                kundeEpost = "test@testesen.com",
                leveringsadresse = IkkeValidertBestilling.IkkeValidertAdresse("Testveien 7", "2070"),
                ordrelinjer = listOf(
                    IkkeValidertBestilling.IkkeValidertOrdrelinje(mengde = "5", produktkode = "MagDust")
                )
            ), time = LocalDateTime.now()
        )
    )
}

// Dependencies
