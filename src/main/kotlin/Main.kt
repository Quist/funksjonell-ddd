import PlasserBestillingWorkflow.*
import java.time.LocalDateTime

fun main(args: Array<String>) {

    val plasserBestillingWorkflow: PlasserBestillingWorkflow =
        initializePlasserBestillingWorkflow(::sjekkProduktKodeEksisterer, ::sjekkAdresseEksisterer, ::hentProduktPris, ::lagBekreftelsesEpostHtml, ::sendBekreftelsesEpost)

    plasserBestillingWorkflow(
        Bestilling(
            IkkeValidertBestilling(
                fakturadresse = "Testveien 07",
                ordreId = "12",
                kundeId = "123",
                kundeEpost = "test@testesen.com",
                leveringsadresse = "Testveien 7",
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

fun sjekkAdresseEksisterer(adresse: String): Boolean {
    return adresse.isNotEmpty()
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