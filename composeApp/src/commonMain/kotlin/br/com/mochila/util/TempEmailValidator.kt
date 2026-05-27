package br.com.mochila.util

// Bloqueio de dominios de email temporario
object TempEmailValidator {

    // Lista local de dominios descartaveis conhecidos
    private val disposableDomains = setOf(
        
        "mailinator.com", "mailinater.com", "mailinator2.com", "mailinator.us",
        
        "guerrillamail.com", "guerrillamail.net", "guerrillamail.org",
        "guerrillamail.biz", "guerrillamail.de", "guerrillamail.info",
        "guerrillamailblock.com", "grr.la", "spam4.me",
        "sharklasers.com", "jourrapide.com", "armyspy.com",
        
        "10minutemail.com", "10minutemail.net", "10minutemail.org",
        "10minutemail.de", "10minutemail.co.uk", "10minemail.com",
        "10mail.org",
        
        "yopmail.com", "yopmail.fr", "yopmail.net", "yopmail.gq",
        "yop.email", "cool.fr.nf", "jetable.fr.nf", "nospam.ze.tc",
        "nomail.xl.cx", "mega.zik.dj", "speed.1s.fr", "courriel.fr.nf",
        "moncourrier.fr.nf", "monemail.fr.nf", "monmail.fr.nf",
        
        "trashmail.com", "trashmail.at", "trashmail.io", "trashmail.me",
        "trashmail.net", "trashmail.xyz", "trashmail.org",
        "trash-mail.at", "trash-mail.cf", "trash-mail.ga",
        "trash-mail.gq", "trash-mail.ml", "trash-mail.tk",
        "trashdevil.com", "trashdevil.de", "trashinbox.com",
        
        "tempmail.com", "tempmail.net", "tempmail.org", "tempmail.eu",
        "temp-mail.org", "temp-mail.io", "temp-mail.ru",
        "tempemail.com", "tempr.email", "tempalias.com",
        "temporaryemail.net", "temporaryemail.us", "temporaryforwarding.com",
        "temporarymailaddress.com", "tempinbox.com", "tempinbox.co.uk",
        "tempomail.fr", "tempmailer.com", "mytempemail.com",
        "timed.email", "one-time.email",
        
        "discard.email", "discardmail.com", "discardmail.de",
        "throwaway.email", "throwam.com", "throwam.net",
        "throwsmoke.com", "dontreg.com",
        
        "getnada.com", "nada.email", "nadaemail.com",
        
        "maildrop.cc",
        
        "mailnull.com",
        
        "dispostable.com",
        
        "spamgourmet.com", "spamgourmet.net", "spamgourmet.org",
        
        "spam.la", "spam.su", "spamex.com", "spamfree.eu",
        "spamfree24.org", "spamgap.com", "spamherelots.com",
        "spamspot.com", "spamthisplease.com", "spamtrail.com",
        
        "fakeinbox.com", "fakemail.net", "fakemailgenerator.com",
        "fake-email.net",
        
        "emailondeck.com",
        
        "wegwerfmail.de", "wegwerfmail.net", "wegwerfmail.org",
        "wegwerfemail.com", "wegwerfemail.de", "wegwerfemail.net", "wegwerfemail.org",
        
        "zehnminuten.de", "zehnminutenmail.de",
        
        "mailnesia.com",
        
        "mohmal.com",
        
        "pookmail.com",
        
        "getairmail.com",
        
        "inboxbear.com",
        
        "safetymail.info", "safetypost.de",
        
        "filzmail.com",
        
        "getonemail.com", "oneoffemail.com", "onewaymail.com",
        
        "dayrep.com", "einrot.com", "fleckens.hu",
        
        "mintemail.com",
        
        "mailexpire.com",
        
        "mailme.ir", "mailmetrash.com", "mailscrap.com",
        "mailsiphon.com", "mailzilla.org",
        
        "mt2009.com", "mt2014.com",
        
        "netmails.net", "netmails.com",
        
        "objectmail.com", "obobbo.com",
        
        "pop3.xyz",
        
        "quickinbox.com",
        
        "skeefmail.com", "slopsbox.com",
        
        "snkmail.com", "sofimail.com",
        
        "trbvm.com",
        
        "nwytg.net",
        
        "tmailinator.com",
        
        "supergreatmail.com",
        
        "kasmail.com", "klzlk.com",
        
        "imails.info",
        
        "rmqkr.net",
        
        "odnorazovoe.ru",
        
        "mailbox52.com",
        
        "yepmail.net",
        
        "xagloo.com", "xemaps.com", "xents.com", "xmaily.com", "xoxy.net",
        
        "spoofmail.de",
        
        "stuffmail.de",
        
        "tafmail.com", "tagyourself.com",
        
        "teewars.org",
        
        "teleworm.com", "teleworm.us",
        
        "thisisnotmyrealemail.com",
        
        "tilien.com",
        
        "toiea.com",
        
        "turual.com", "twinmail.de",
        
        "uroid.com",
        
        "venompen.com",
        
        "wilemail.com",
        
        "wuzupmail.net",
        
        "z1p.biz",
        
        "yomail.info",
        
        "outlawspam.com",
        
        "otherinbox.com",
        
        "crazymailing.com",
        
        "emailfake.com", "emailfake.ml",
        
        "tempail.com", "tempemail.net",
    )

    // Verifica dominio na lista local
    fun isDisposable(email: String): Boolean {
        val domain = extractDomain(email)
        return domain.isNotEmpty() && domain in disposableDomains
    }

    // Consulta lista local e API externa
    suspend fun isDisposableAsync(email: String): Boolean {
        if (isDisposable(email)) return true
        val domain = extractDomain(email)
        return domain.isNotEmpty() && checkDisposableApi(domain)
    }

    private fun extractDomain(email: String): String =
        email.substringAfter("@", "").lowercase().trimEnd('.')
}
