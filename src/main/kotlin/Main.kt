import PlasserBestillingWorkflow.SjekkAdresseEksisterer
import PlasserBestillingWorkflow.SjekkProduktKodeEksisterer

fun main(args: Array<String>) {

    // plasserBestilling(IkkeValidertBestilling(ordreId = "1", kundeinfo = "Adam Åndra", "Tsjekkia", emptyList()))
}

// Hjelpefunksjoner, eksterne services.
val sjekkProduktKodeEksisterer: SjekkProduktKodeEksisterer = { produktKode ->
    val gyldigeProdukter = setOf("MagDust", "Magnus Tskjorte")
    produktKode in gyldigeProdukter
}
val sjekkAdresseEksisterer: SjekkAdresseEksisterer = { adresse -> adresse.isNotEmpty() }