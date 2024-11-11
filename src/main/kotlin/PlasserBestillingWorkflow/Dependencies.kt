package PlasserBestillingWorkflow

// ==================================
// Eksempelimplementasjoner av eksterne tjenester
// ==================================

fun sjekkProduktKodeEksisterer(produktKode: String): Boolean {
    return produktKode in produktPriser
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

fun hentProduktPris(produktKode: Produktkode): Double {
    return produktPriser[produktKode.value] ?: throw FantIkkeProduktkode("Fant ikke produktkode: $produktKode");
}

// Lager HTML for bekreftelseseposten
fun lagBekreftelsesEpostHtml(prisetBestilling: PrisetBestilling): HtmlString {
    return "<h1> Bestilling bekreftet!</h1>"
}

fun sendBekreftelsesEpost(email: String, letter: HtmlString): SendEpostResultat {
    return SendEpostResultat.Sendt
}

fun sjekkEpostStatus(email: String): Boolean {
    // Returnerer true hvis e-posten finnes i listen over verifiserte e-poster
    return email in verifiserteEposter
}

// Testdata
val produktPriser = mapOf(
    "MagDust" to 199.0,
    "MagTee" to 349.0,
    "MagShoes" to 999.0,
    "Klatresele" to 849.0,
    "Klatretau" to 1299.0,
    "Kalkpose" to 149.0,
    "Sikringsbrems" to 599.0,
    "Klatrehjelm" to 749.0,
    "KortSlynge" to 129.0,
    "Karabinkrok" to 89.0,
    "Klatrebørste" to 59.0,
    "CrashPad" to 2499.0
)

val verifiserteEposter = listOf(
    "adam.ondra@climbing.com",
    "alex.honnold@climbing.com",
    "lynn.hill@climbing.com",
    "tommy.caldwell@climbing.com",
    "chris.sharma@climbing.com"
)