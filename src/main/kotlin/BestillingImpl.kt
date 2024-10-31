import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getOrThrow
import tjenester.ValidertAdresse
import utils.NonEmptyList

// Eksempelimplementasjon av PlasserBestilling

val plasserBestilling: PlasserBestilling = { ikkeValidertBestilling ->
    if (ikkeValidertBestilling.ordreId.isEmpty()) {
        Err("OrdreId er tom!")
    }

    val validertBestilling = ValidertBestilling(
        id = ikkeValidertBestilling.ordreId,
        fakturaAdresse = "placeholder",
        kundeId = "placeholder",
        leveringsadresse = ValidertAdresse(""),
        ordrelinjer = NonEmptyList.fromList(ikkeValidertBestilling.ordrelinjer.map { ordrelinje ->
            Ordrelinje(
                id = "",
                ordreId =  "",
                produktkode = Produktkode.Klatreutstyr(KlatreutstyrKode("")),
                ordreMengde = OrdreMengde.Enhet(Enhetsmengde(2)),
                pris = ""
            )
        }).getOrThrow { UgyldigOrdreException() },
        sumSomSkalBliBelastet = "Nothing"
    )
    Ok(BestillingPlassertHendelser(bekreftelseSent = true, ordrePlassert = true, fakturerbarOrdrePlassert = true))
}
