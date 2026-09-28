# Nibras · نِبْراس

**A lamp for the seeker of knowledge.**

Nibras is an Islamic knowledge library for Android: authentic books, the stories of the Prophets and the Companions, daily remembrances, and lectures from trusted Sunni scholars — presented in a calm, premium reading experience, fully in **Arabic and English**, and working completely **offline**.

Built by **M.H.M.A**.

---

## Features

- **Library of 15 books** (151 chapters, ~35,000 words per language) in both Arabic and English:
  - *Understanding the Qur'an* · *The Forty Hadith of Imam al-Nawawi* · *Gems of Prophetic Wisdom*
  - *Pillars of Islam & Faith* · *The Beautiful Names of Allah*
  - *Stories of the Prophets* · *The Life of the Prophet ﷺ*
  - *Stars of the Companions* · *Women of Light*
  - *Purification of the Heart* · *Fortress of the Believer (daily adhkar)* · *Prophetic Manners*
  - *Worship Made Easy* · *The Rightly Guided Caliphs* · *Lights of Islamic Civilisation*
- **Beautiful reader** with adjustable text size, light / sepia / dark page colours, chapter navigation, a contents panel, reading progress and "keep screen on". Arabic is set in *Amiri*, English in *Lora*.
- **Full-text search** across every book in either language, with Arabic diacritics and letter variants normalised.
- **Home** with the Hijri date, a daily reminder from the Qur'an and Sunnah, "continue reading", categories and featured books.
- **Lectures**: curated official YouTube channels of well-known scholars in English and Arabic, plus Qur'an recitation.
- **Bookmarks**, per-book reading language, reading statistics and progress reset.
- **Arabic and English interface** with full right-to-left support; switch language or appearance (light / dark / system) at any time.
- **Procedural cover art** and Islamic geometric patterns drawn on the device — no image downloads, no tracking, no ads.

## Technical overview

| | |
|---|---|
| Language | Kotlin |
| UI | Android framework views, Material theme, custom views (`CoverView`, `PatternView`) |
| Minimum / target SDK | 26 / 36 |
| Dependencies | None at runtime — only the Android framework and the Kotlin standard library |
| Persistence | `SharedPreferences` (settings, bookmarks, progress) |
| Content | Plain-text book files in `app/src/main/assets/books` with a tiny markup, parsed at runtime |
| Tests | JUnit tests for the parser, catalogue, search and every content file |

Keeping the app free of third-party libraries keeps the APK small (a few megabytes, most of it fonts and text), start-up instant, and the build simple.

### Project layout

```
app/src/main
├── AndroidManifest.xml
├── assets/
│   ├── books/            <id>.en.txt and <id>.ar.txt for every book
│   └── licenses/         Open Font License texts
├── java/com/mhma/nibras
│   ├── NibrasApp.kt      application singleton
│   ├── core/             Prefs, LocaleHelper, BaseActivity, HijriDate, UI helpers
│   ├── content/          models, markup parser, catalogue, repository, search, wisdom, lectures
│   └── ui/               screens (home, library, lectures, more, book detail, reader, search, about)
└── res/                  themes (light/dark), fonts, vector icons, layouts, strings (en/ar)
```

### Content format

Each book is a UTF-8 text file. Titles and descriptions live in `Catalog.kt`; the text uses a minimal markup:

```
== Chapter title
Paragraph text. Lines are joined; a blank line ends a paragraph.
## Sub-heading
> A quoted text (a hadith or verse). Consecutive quote lines keep their breaks.
~ Source or attribution of the quote
! A highlighted note
- A bullet item
```

To add a book: create `books/<id>.en.txt` and `books/<id>.ar.txt`, add an entry to `Catalog.books` with the chapter count, and run the unit tests — they verify that both files parse, that the chapter counts match, and that the structure is identical in both languages.

## Building

Open the project in Android Studio (Ladybug or newer) and press Run, or from the command line:

```
./gradlew assembleDebug        # debug APK
./gradlew testDebugUnitTest    # unit tests
./gradlew assembleRelease      # shrunk release build (needs a signing config)
```

## Design

- Palette: deep emerald, warm ivory and gold, with a matching dark theme.
- Typography: IBM Plex Sans Arabic for the interface, Amiri for Arabic reading and headings, Lora for English reading and headings.
- Everything is drawn with vectors and code, so the app looks sharp on every screen density.

## Fonts and licences

The bundled typefaces are used under the SIL Open Font License 1.1: **Amiri** (Khaled Hosny and the Amiri Project Authors), **IBM Plex Sans Arabic** (IBM Corp.) and **Lora** (Cyreal). The licence texts are included in the app under *More → Open-source licences*.

The classical texts (the Forty Hadith, Qur'anic verses, authentic hadith and supplications) are quoted from their sources; the explanations, stories and guides are original writing for this app. If you find a mistake, please report it so it can be corrected — knowledge is a trust.
