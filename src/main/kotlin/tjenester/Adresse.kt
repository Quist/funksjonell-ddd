package tjenester


typealias AdresseService = (IkkeValidertBestilling.UvalidertAdresse) -> ValidertAdresse

val validerAdresse = {adresseService: AdresseService ->
     ValidertAdresse("")
}




data class ValidertAdresse private constructor(val adresselinje: String) {

}
