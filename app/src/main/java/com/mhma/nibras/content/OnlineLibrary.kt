package com.mhma.nibras.content

/**
 * A classical work that can be read in full, for free, on the
 * Jami' al-Kutub al-Islamiyya website (ketabonline.com).
 */
class OnlineBook(
    /** Category of the in-app library this work belongs to. */
    val categoryId: String,
    val title: Localized,
    val author: Localized,
    val description: Localized,
    /** Icon key resolved to a drawable by the UI layer. */
    val icon: String,
    /** Index into the cover palettes (0..7). */
    val palette: Int,
    /** Numeric id of the book on the website. */
    val siteId: Int
) {
    /** The book's page on the website, with its table of contents and reader. */
    val url: String get() = OnlineLibrary.bookUrl(siteId)
}

/**
 * The classical library: the great works of the scholars, digitised and
 * offered for free reading by Jami' al-Kutub al-Islamiyya. Every entry was
 * checked against the site so that each link opens the intended edition.
 */
object OnlineLibrary {

    const val SITE_HOST = "ketabonline.com"
    const val HOME_URL = "https://ketabonline.com/ar"
    const val QURAN_URL = "https://ketabonline.com/ar/quran"
    const val NARRATORS_URL = "https://ketabonline.com/ar/narrators"

    val siteName: Localized = Localized("Jami' al-Kutub al-Islamiyya", "جامع الكتب الإسلامية")

    fun bookUrl(siteId: Int): String = "https://ketabonline.com/ar/books/$siteId"

    val items: List<OnlineBook> = listOf(
        // ---- Qur'an -------------------------------------------------------
        OnlineBook(
            "quran",
            Localized("Tafsir Ibn Kathir", "تفسير القرآن العظيم"),
            Localized("Ibn Kathir (d. 774 AH)", "الحافظ ابن كثير (ت ٧٧٤هـ)"),
            Localized(
                "The most widely read classical commentary: the Qur'an explained by the Qur'an, the Sunnah and the words of the Companions. Dar Tayba edition.",
                "أشهر التفاسير بالمأثور: تفسير القرآن بالقرآن والسنة وأقوال الصحابة. طبعة دار طيبة."
            ),
            "book_open", 0, 500050
        ),
        OnlineBook(
            "quran",
            Localized("Tafsir al-Sa'di", "تفسير السعدي (تيسير الكريم الرحمن)"),
            Localized("Abd al-Rahman al-Sa'di (d. 1376 AH)", "الشيخ عبد الرحمن السعدي (ت ١٣٧٦هـ)"),
            Localized(
                "A clear, modern commentary that draws out the meaning and the lessons of every passage in simple language.",
                "تفسير واضح ميسّر يستخرج معاني الآيات وفوائدها بعبارة سهلة لكل قارئ."
            ),
            "sparkle", 0, 4738
        ),

        // ---- Hadith -------------------------------------------------------
        OnlineBook(
            "hadith",
            Localized("Sahih al-Bukhari", "صحيح البخاري"),
            Localized("Imam al-Bukhari (d. 256 AH)", "الإمام البخاري (ت ٢٥٦هـ)"),
            Localized(
                "The most authentic book after the Qur'an. Dar Tawq al-Najah edition with the standard hadith numbering.",
                "أصح كتاب بعد كتاب الله. طبعة دار طوق النجاة بترقيم محمد فؤاد عبد الباقي."
            ),
            "scroll", 1, 2130
        ),
        OnlineBook(
            "hadith",
            Localized("Sahih Muslim", "صحيح مسلم"),
            Localized("Imam Muslim (d. 261 AH)", "الإمام مسلم (ت ٢٦١هـ)"),
            Localized(
                "The second of the two Sahihs, famous for gathering the narrations of each hadith in one place.",
                "ثاني الصحيحين، اشتهر بجمع طرق الحديث الواحد في موضع واحد."
            ),
            "scroll", 1, 1003010
        ),
        OnlineBook(
            "hadith",
            Localized("Riyad al-Salihin", "رياض الصالحين"),
            Localized("Imam al-Nawawi (d. 676 AH)", "الإمام النووي (ت ٦٧٦هـ)"),
            Localized(
                "The Gardens of the Righteous: hadith on manners, worship and the heart, arranged by topic. Al-Risalah edition edited by Shu'ayb al-Arna'ut.",
                "أحاديث الآداب والعبادات وأعمال القلوب مرتبة على الأبواب. طبعة الرسالة بتحقيق شعيب الأرنؤوط."
            ),
            "favorite", 1, 1102
        ),
        OnlineBook(
            "hadith",
            Localized("Sharh Riyad al-Salihin", "شرح رياض الصالحين"),
            Localized("Ibn Uthaymeen (d. 1421 AH)", "الشيخ ابن عثيمين (ت ١٤٢١هـ)"),
            Localized(
                "A warm, practical explanation of Riyad al-Salihin in six volumes by the beloved teacher of Unayzah.",
                "شرح عملي دافئ لرياض الصالحين في ستة مجلدات، من دروس الشيخ في عنيزة."
            ),
            "quote", 1, 6350
        ),
        OnlineBook(
            "hadith",
            Localized("The Forty Hadith, with Ibn Rajab's additions", "الأربعون النووية مع زيادات ابن رجب"),
            Localized("Imam al-Nawawi (d. 676 AH)", "الإمام النووي (ت ٦٧٦هـ)"),
            Localized(
                "The forty-two foundational hadith of this app's own edition, plus the eight that Ibn Rajab added to complete fifty.",
                "الأحاديث الاثنان والأربعون التي في مكتبة نبراس، مع الأحاديث الثمانية التي زادها الحافظ ابن رجب لتكمل الخمسين."
            ),
            "scroll", 1, 42787
        ),
        OnlineBook(
            "hadith",
            Localized("Fath al-Bari", "فتح الباري شرح صحيح البخاري"),
            Localized("Ibn Hajar al-Asqalani (d. 852 AH)", "الحافظ ابن حجر العسقلاني (ت ٨٥٢هـ)"),
            Localized(
                "The monumental commentary on Sahih al-Bukhari, thirteen volumes with the notes of Sheikh Ibn Baz.",
                "الشرح الأعظم لصحيح البخاري في ثلاثة عشر مجلدًا، مع تعليقات الشيخ ابن باز."
            ),
            "book_open", 1, 2122
        ),
        OnlineBook(
            "hadith",
            Localized("Bulugh al-Maram", "بلوغ المرام من أدلة الأحكام"),
            Localized("Ibn Hajar al-Asqalani (d. 852 AH)", "الحافظ ابن حجر العسقلاني (ت ٨٥٢هـ)"),
            Localized(
                "The hadith of Islamic law arranged by chapter, the text memorised by students of fiqh everywhere.",
                "أحاديث الأحكام مرتبة على أبواب الفقه، المتن الذي يحفظه طلاب العلم في كل مكان."
            ),
            "scroll", 6, 6302
        ),

        // ---- Creed ----------------------------------------------------------
        OnlineBook(
            "aqeedah",
            Localized("Sharh al-Aqidah al-Tahawiyyah", "شرح العقيدة الطحاوية"),
            Localized("Ibn Abi al-Izz al-Hanafi (d. 792 AH)", "ابن أبي العز الحنفي (ت ٧٩٢هـ)"),
            Localized(
                "The classic explanation of Imam al-Tahawi's creed, the text on which the Sunni schools agree. Edited by al-Arna'ut and al-Turki.",
                "الشرح المعتمد لعقيدة الإمام الطحاوي، المتن الذي اتفق عليه أهل السنة. تحقيق الأرنؤوط والتركي."
            ),
            "kaaba", 2, 6001
        ),

        // ---- Prophets & Seerah -----------------------------------------------
        OnlineBook(
            "prophets",
            Localized("Stories of the Prophets", "قصص الأنبياء"),
            Localized("Ibn Kathir (d. 774 AH)", "الحافظ ابن كثير (ت ٧٧٤هـ)"),
            Localized(
                "The lives of the prophets from Adam to Jesus, told from the Qur'an and authentic reports by the master historian.",
                "سير الأنبياء من آدم إلى عيسى عليهم السلام، من القرآن والأخبار الصحيحة بقلم المؤرخ الحافظ."
            ),
            "crescent", 4, 228
        ),
        OnlineBook(
            "prophets",
            Localized("Al-Raheeq al-Makhtum", "الرحيق المختوم"),
            Localized("Safi al-Rahman al-Mubarakpuri (d. 1427 AH)", "صفي الرحمن المباركفوري (ت ١٤٢٧هـ)"),
            Localized(
                "The Sealed Nectar: the award-winning biography of the Prophet ﷺ, complete and accessible in one volume.",
                "السيرة النبوية الحائزة على الجائزة الأولى في مسابقة رابطة العالم الإسلامي، كاملة في مجلد واحد."
            ),
            "crescent", 4, 6926
        ),
        OnlineBook(
            "prophets",
            Localized("Zad al-Ma'ad", "زاد المعاد في هدي خير العباد"),
            Localized("Ibn al-Qayyim (d. 751 AH)", "الإمام ابن القيم (ت ٧٥١هـ)"),
            Localized(
                "Provisions for the Hereafter: the guidance of the Prophet ﷺ in worship, dealings, medicine and daily life.",
                "هدي النبي ﷺ في عباداته ومعاملاته وطبّه وحياته اليومية، في خمسة مجلدات."
            ),
            "lantern", 4, 2761
        ),

        // ---- Companions --------------------------------------------------------
        OnlineBook(
            "companions",
            Localized("Siyar A'lam al-Nubala'", "سير أعلام النبلاء"),
            Localized("Al-Dhahabi (d. 748 AH)", "الإمام الذهبي (ت ٧٤٨هـ)"),
            Localized(
                "The Lives of Noble Figures: biographies of the Companions, the Followers and the scholars of every age, in twenty-five volumes.",
                "تراجم الصحابة والتابعين وعلماء الأمة في كل عصر، في خمسة وعشرين مجلدًا. طبعة الرسالة."
            ),
            "people", 3, 598
        ),

        // ---- Heart & Soul -------------------------------------------------------
        OnlineBook(
            "spirituality",
            Localized("Madarij al-Salikin", "مدارج السالكين"),
            Localized("Ibn al-Qayyim (d. 751 AH)", "الإمام ابن القيم (ت ٧٥١هـ)"),
            Localized(
                "The Stations of the Seekers: the journey of the heart to Allah between \"You alone we worship\" and \"You alone we ask for help\".",
                "منازل القلب في سيره إلى الله بين «إياك نعبد» و«إياك نستعين»."
            ),
            "tasbih", 5, 6012
        ),
        OnlineBook(
            "spirituality",
            Localized("Mukhtasar Minhaj al-Qasidin", "مختصر منهاج القاصدين"),
            Localized("Ibn Qudamah al-Maqdisi (d. 689 AH)", "ابن قدامة المقدسي (ت ٦٨٩هـ)"),
            Localized(
                "A concise, authentic guide to worship, manners, the destructive traits and the saving ones.",
                "دليل موجز موثوق في العبادات والعادات والمهلكات والمنجيات."
            ),
            "tasbih", 5, 6905
        ),
        OnlineBook(
            "spirituality",
            Localized("Al-Da' wa al-Dawa'", "الداء والدواء (الجواب الكافي)"),
            Localized("Ibn al-Qayyim (d. 751 AH)", "الإمام ابن القيم (ت ٧٥١هـ)"),
            Localized(
                "The Disease and the Cure: how sins harm the heart and how repentance, prayer and remembrance heal it.",
                "كيف تضرّ الذنوب بالقلب، وكيف تشفيه التوبة والدعاء والذكر."
            ),
            "favorite", 5, 22
        ),

        // ---- Worship -------------------------------------------------------------
        OnlineBook(
            "worship",
            Localized("Al-Adhkar", "الأذكار"),
            Localized("Imam al-Nawawi (d. 676 AH)", "الإمام النووي (ت ٦٧٦هـ)"),
            Localized(
                "The complete collection of the Prophet's remembrances and supplications for every moment of the day and night.",
                "الجامع لأذكار النبي ﷺ وأدعيته في كل أحوال اليوم والليلة. تحقيق عبد القادر الأرنؤوط."
            ),
            "tasbih", 6, 2506
        ),
        OnlineBook(
            "worship",
            Localized("Hisn al-Muslim", "حصن المسلم من أذكار الكتاب والسنة"),
            Localized("Sa'id ibn Ali al-Qahtani", "الشيخ سعيد بن علي القحطاني"),
            Localized(
                "Fortress of the Muslim: the pocket book of daily adhkar from the Qur'an and the authentic Sunnah, in its original Arabic.",
                "كتيب الأذكار اليومية من الكتاب والسنة الصحيحة، النسخة الأصلية بترقيمها."
            ),
            "mosque", 6, 3969
        ),

        // ---- History --------------------------------------------------------------
        OnlineBook(
            "history",
            Localized("Al-Bidayah wa al-Nihayah", "البداية والنهاية"),
            Localized("Ibn Kathir (d. 774 AH)", "الحافظ ابن كثير (ت ٧٧٤هـ)"),
            Localized(
                "The Beginning and the End: the history of the world from creation, through the Prophets and the Islamic centuries, to the signs of the Hour. Dar Hajar edition.",
                "تاريخ العالم من بدء الخلق ومرورًا بالأنبياء والقرون الإسلامية إلى أشراط الساعة. طبعة هجر."
            ),
            "history", 7, 4767
        )
    )

    fun itemsIn(categoryId: String): List<OnlineBook> = items.filter { it.categoryId == categoryId }

    /** Ids of the categories that have at least one work, in catalogue order. */
    fun categoryIds(): List<String> = Catalog.categories.map { it.id }.filter { id -> items.any { it.categoryId == id } }
}
