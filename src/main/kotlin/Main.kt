import PlasserBestillingWorkflow.Bestilling
import PlasserBestillingWorkflow.IkkeValidertBestilling
import PlasserBestillingWorkflow.PlasserBestillingWorkflow
import PlasserBestillingWorkflow.initializePlasserBestillingWorkflow
import java.time.LocalDateTime

fun main(args: Array<String>) {
    val plasserBestillingWorkflow: PlasserBestillingWorkflow =
        initializePlasserBestillingWorkflow(::sjekkProduktKodeEksisterer, ::sjekkAdresseEksisterer)
    plasserBestillingWorkflow(
        Bestilling(
            IkkeValidertBestilling(
                fakturadresse = "Testveien 07",
                ordreId = "12",
                kundeinfo = "123",
                leveringsadresse = "Testveien 7",
                ordrelinjer = listOf(
                    IkkeValidertBestilling.IkkeValidertOrdrelinje(mengde = 5, produktkode = "MagDust")
                )
            ), time = LocalDateTime.now()
        )
    )
}

// Hjelpefunksjoner, eksterne services.
fun sjekkProduktKodeEksisterer(produktKode: String): Boolean {
    val gyldigeProdukter = setOf("MagDust", "MagTee", "MagShoes")
    return produktKode in gyldigeProdukter
}

fun sjekkAdresseEksisterer(adresse: String): Boolean {
    return adresse.isNotEmpty()
}