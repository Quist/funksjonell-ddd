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
                    IkkeValidertBestilling.IkkeValidertOrdrelinje(mengde = 5, produktkode = "MagDust")
                )
            ), time = LocalDateTime.now()
        )
    )
}

// Dependencies
fun sjekkProduktKodeEksisterer(produktKode: String): Boolean {
    val gyldigeProdukter = setOf("MagDust", "MagTee", "MagShoes")
    return produktKode in gyldigeProdukter
}

fun sjekkAdresseEksisterer(adresse: IkkeValidertBestilling.IkkeValidertAdresse): Boolean {
    return !adresse.postnummer.isNullOrBlank() || !adresse.gateadresse.isNullOrBlank()
}

fun hentProduktPris(produktKode: Produktkode): Int {
    return 5;
}

fun lagBekreftelsesEpostHtml(prisetBestilling: PrisetBestilling): HtmlString {
    return "<h1>Bekreftet!</h1>"
}

fun sendBekreftelsesEpost(email: String, letter: HtmlString): SendEpostResultat {
    return SendEpostResultat.Sendt
}