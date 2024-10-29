import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

// Eksempelimplementasjon av PlasserBestilling

val plasserBestilling: PlasserBestilling = {ikkeValidertBestilling ->
    if (ikkeValidertBestilling.ordreId.isEmpty()) {
        Err("OrdreId er tom!")
    }
    Ok(BestillingPlassertHendelser(bekreftelseSent = true, ordrePlassert = true, fakturerbarOrdrePlassert = true))
}
