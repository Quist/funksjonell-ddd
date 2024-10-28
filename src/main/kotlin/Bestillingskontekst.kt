import com.github.michaelbull.result.Result

// Representerer vår først workflow. En funksjon som tar imot en "rå" bestilling og konverterer den til hendelser.
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
value class Enhetsmengde(val value: Int)

@JvmInline
value class Kilogrammengde(val value: Float)

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
    private val ordreId: String,
    private val kundeinfo: String,
    private val leveringsadresse: String
)

// Representerer outputten av PlasserBestilling - ulike hendelser
data class BestillingPlassertHendelser(
    private val bekreftelseSent: Nothing,
    private val ordrePlassert: Nothing,
    private val FakturerbarOrdrePlassert: Nothing
)
typealias PlasserBestillingFeil = Nothing

