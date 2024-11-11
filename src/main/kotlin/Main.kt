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
fun sjekkProduktKodeEksisterer(produktKode: String): Boolean {
    val gyldigeProdukter = setOf("MagDust", "MagTee", "MagShoes")
    return produktKode in gyldigeProdukter
}

fun sjekkAdresseEksisterer(adresse: IkkeValidertBestilling.IkkeValidertAdresse): Boolean {
    val eksisterendeAdresser = listOf(
        "Karl Johans gate" to "1234",
        "Bryggen" to "5678",
        "Olav Tryggvasons gate" to "9012",
        "Hillevågsveien" to "3456",
        "Storgata" to "7890"
    )
    if (adresse.postnummer.isNullOrBlank() || adresse.gateadresse.isNullOrBlank()) {
        return false
    }
    return eksisterendeAdresser.contains(adresse.gateadresse to adresse.postnummer)
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

fun sjekkEpostStatus(email: String): Boolean {
    // Liste over verifiserte e-poster for berømte klatrere
    val verifiserteEposter = listOf(
        "adam.ondra@climbing.com",
        "alex.honnold@climbing.com",
        "lynn.hill@climbing.com",
        "tommy.caldwell@climbing.com",
        "chris.sharma@climbing.com"
    )

    // Returnerer true hvis e-posten finnes i listen over verifiserte e-poster
    return email in verifiserteEposter
}