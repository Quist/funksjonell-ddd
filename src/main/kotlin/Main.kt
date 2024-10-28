fun main(args: Array<String>) {
    println("Hello World!")

    val nyKunde = KundeId(2);
    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}


// Lagge byggeklosser for systemet

// Simple types
@JvmInline
value class KundeId(val id: Int)

@JvmInline
value class Mengde(val mengde: Int)

@JvmInline
value class KilogramMengde(val mengde: Int)

// Complex data
data class Ordre(
    private val leveringsaddresse: Leveringsadresse
    // TODOﬁﬁ
)

typealias Kundeinfo = Nothing
typealias Leveringsadresse = Nothing
typealias Betalingsadresse = Nothing
typealias Ordrelinje = Nothing
typealias Regningssum = Nothing