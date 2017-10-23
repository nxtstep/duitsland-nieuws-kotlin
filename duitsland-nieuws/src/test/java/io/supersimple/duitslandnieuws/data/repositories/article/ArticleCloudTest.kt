package io.supersimple.duitslandnieuws.data.repositories.article

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.supersimple.duitslandnieuws.data.api.ArticleEndpoint
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArticleCloudTest {

    lateinit var networkService: ArticleEndpoint

    private val gsonConverter: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()

    @get:Rule
    val wireMock = WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort())

    private lateinit var baseUrl: String

    @Before
    fun setup() {
        baseUrl = "http://localhost:${wireMock.port()}"

        networkService = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gsonConverter))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ArticleEndpoint::class.java)
    }

    @Test
    fun testList() {
        stubFor(
                get(urlEqualTo("/posts?page=1&per_page=10"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withStatus(200)
                                        .withBodyFile("posts_page_1_10.json")
                        )
        )

        val cloud = ArticleCloud(networkService)
        cloud.list(0, 10)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue { it.size == 10 }
                .assertValue { it[0].id == "49978" }
                .assertValue { it[0].slug == "wensdenken" }
                .assertValue { it[0].link == "http://duitslandnieuws.nl/blog/2017/01/08/wensdenken/" }
                .assertValue { it[0].excerpt.rendered == "Hij was amper 20 jaar oud en stond in de rij voor een vrachtauto die hem terug naar voren zou brengen. Naar voren. Dat was wel het laatste waar hij op zat te wachten op deze koude dag in januari. Een maand waren ze hier. Eigenlijk nog iets minder, maar het voelde als een jaar. Sinds 16 december had hij nauwelijks geslapen en het doorlopend koud gehad. Een kilometer of 15 waren ze uiteindelijk teruggetrokken. Nu dus weer de andere kant op.<a class=\"read-more\" href=\"http://duitslandnieuws.nl/blog/2017/01/08/wensdenken/\">--- meer ---</a>" }
                .assertValue { it[0].content.rendered == "<p>Deze Amerikaanse boerenknecht zat in de nasleep van Hitlers laatste grote offensief in het westen. Zijn onervaren 106e divisie moest de verrassingsaanval in de Ardennen opvangen. Op papier verdedigden ze een besneeuwd front van 5 kilometer, maar in de praktijk was het vijf keer zo lang.</p>\n<p>Het werd een bloedbad en in vier dagen verloor de divisie ruim 7.000 soldaten. De onervaren 106e werd onder de voet gelopen, maar dankzij heldhaftig optreden van Amerikaanse Parachutisten bij Bastenaken en een snelle Amerikaanse tegenaanval door Patton kon de Duitse opmars tot staan worden gebracht en werd het front in Belgi\u00eb gered.</p>\n<h2>Tunnelvisie</h2>\n<p>Een machtig verhaal, prachtig beschreven en ook fraai verfilmd. Maar de realiteit is een andere.</p>\n<p>In werkelijkheid werd dat offensief tot op de minuut voorspeld. De logistiek werd al maanden in de gaten gehouden. Het transport van zware tanks via de rangeerterreinen van Frankfurt was in beeld en de troepenopbouw op verzamelpunten werd\u00a0dagelijks gefotografeerd. Pantserdivisies, parachutisten en commandanten waren in kaart. Zelfs de geheimzinnige speurtocht van de nazi&#8217;s naar Amerikaanse uniformen en jeeps was bekend. Toch deed het geallieerde opperbevel niets.</p>\n<p>Tunnelvisie en persoonlijke ambities stonden effectief ingrijpen in de weg. Een generaal wordt nu eenmaal zelden op het schild gehesen voor het voorkomen van een groot offensief. Bovendien wilde het er bij de meeste commandanten niet in dat de nazi\u2019s met de totale nederlaag in zicht nog een groot zinloos offensief door onbegaanbaar terrein zouden lanceren. Ze\u00a0waren verslagen en voor de geallieerde leiding\u00a0telde uitsluitend nog de persoonlijke beloning voor Hamburg, Berlijn en M\u00fcnchen. Er werden geen extra troepen richting de Ardennen gestuurd om de overbelaste onervaren soldaten te ondersteunen.</p>\n<h2>Achteraf</h2>\n<p>Van de zeven betrokken commandanten luisterde \u00e9\u00e9n Amerikaanse generaal uiteindelijk toch een beetje naar zijn inlichtingenofficier. Hij hield \u00e9\u00e9n tankdivisie in reserve om via een snelle tegenaanval de schade beperkt te houden. Meer wilde hij niet inleveren, want dan zou hij de race om de prestigieuze prijzen verliezen.</p>\n<p>Het resultaat was het Ardennenoffensief met ruim 150.000 slachtoffers. Onnodig en te voorkomen als wensdenken ondergeschikt was gemaakt aan de feiten.</p>\n<p>De enorme inspanning die de 106e divisie zonder enige hulp de eerste twee dagen rond St. Vith (*) leverde was essentieel voor de geallieerde overwinning. Maar toen de rook was opgetrokken gingen de onderscheidingen naar de tankdivisie die veel te laat de tegenaanval inzette en de parachutisten die Bastenaken hielden. Het opperbevel gooide het achteraf op een verrassingsaanval en maakte er in rapporten een heldhaftig verhaal van waarin bevelhebbers snel schakelden om verliezen beperkt te houden.</p>\n<p>Ze wonnen, dus ze deugen.</p>\n<p>Toch?</p>\n<p>&nbsp;</p>\n<p><em>* De beroemde Amerikaanse schrijver Kurt Vonnegut was verkenner bij de 106e divisie en werd in de eerste dagen van het Ardennenoffensief gevangen genomen. Op weg naar het gevangenkamp werd zijn trein door Engelsen gebombardeerd en daarbij kwamen talloze Amerikaanse krijgsgevangen om het leven. In zijn beroemd geworden boek \u2018Slaughterhouse 5\u2019 komen zijn ervaringen aan het front en de aanval op de trein samen met wat hij van dichtbij meemaakte tijdens en na het zinloze bombardement op Dresden.</em></p>\n\n<!-- WpssoRrssbSharing::get_buttons content filter skipped: buttons_on_index not enabled -->\n" }
    }

    @Test
    fun testGet() {
        stubFor(
                get(urlEqualTo("/posts/test-id"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withStatus(200)
                                        .withBodyFile("post_50152.json")
                        )
        )

        val cloud = ArticleCloud(networkService)
        cloud.get("test-id")
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { it.id == "50152" }
                .assertValue { it.excerpt.rendered == "Geert Wilders voerde afgelopen weekend campagne voor de AfD en stelde dat met Frauke Petry de toekomst van Duitsland verzekerd is. Tijdens de bijeenkomst van de Europese rechts-populisten in Koblenz bleek waarom Petry de Nederlandse PVV-leider goed kan gebruiken.<a class=\"read-more\" href=\"http://duitslandnieuws.nl/blog/2017/01/23/waarom-frauke-petry-geert-wilders-hard-nodig/\">--- meer ---</a>" }

    }
}