import com.github.michaelbull.result.Result

// Representerer vår først workflow. En funksjon som tar en uvalidert bestilling og konverterer den til hendelser.
typealias PlasserBestilling = (IkkeValidertBestilling) -> Result<BestillingPlassertHendelser, PlasserBestillingFeil>

// Produktkode
@JvmInline
value class KlatreutstyrKode(val value: String)

@JvmInline
value class TskjorteKode(val value: String)

sealed class Produktkode {
    data class Klatreutstyr(val kode: KlatreutstyrKode) : Produktkode()
    data class Tskjorte(val kode: TskjorteKode) : Produktkode()
}

// Ordremengde typer



@JvmInline
value class Kilogrammengde private constructor(val value: Float) {
    companion object {
        fun of(value: Float): Kilogrammengde {
            if (value <=0) {
                throw IllegalStateException("Kilogrammengde må være positiv")
            }
            return Kilogrammengde(value)
        }
    }
}

// TODO Oppgave 2b: Implmenter logikk for å hindre at enhetsmengde kan være et ugyldig tall
@JvmInline
value class Enhetsmengde(val value: Int)

sealed class OrdreMengde {
    data class Enhet(val mengde: Enhetsmengde) : OrdreMengde()
    data class Kilo(val mengde: Kilogrammengde) : OrdreMengde()
}


// Modelere en ordre
data class Ordre(
    private val id: OrdreId,
    private val kundeId: KundeId,
    private val leveringsadresse: Leveringsadresse,
    private val fakturaAddresse: FakturaAddresse,
    private val ordrelinjer: List<OrdrelinjeId>,
    private val sumSomSkalBliBelastet: FakturaSum
)

data class Ordrelinje(
    private val id: OrdrelinjeId,
    private val ordreId: OrdreId,
    private val produktkode: Produktkode,
    private val ordreMengde: OrdreMengde,
    private val pris: Pris
)

typealias OrdreId = Nothing
typealias KundeId = Nothing
typealias Leveringsadresse = Nothing
typealias FakturaAddresse = Nothing
typealias OrdrelinjeId = Nothing
typealias FakturaSum = Nothing
typealias Pris = Nothing


// Representerer en bestilling som har kommet inn i systemet. Ingenting er validert enda, så kun primitive typer.
data class IkkeValidertBestilling(
    val ordreId: String,
    val kundeinfo: String,
    val leveringsadresse: String
)

// Representerer outputten av PlasserBestilling - ulike hendelser
data class BestillingPlassertHendelser(
    val bekreftelseSent: Boolean,
    val ordrePlassert: Boolean,
    val fakturerbarOrdrePlassert: Boolean
)
typealias PlasserBestillingFeil = String

