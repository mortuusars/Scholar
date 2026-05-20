# Changelog

## UNRELEASED
- Inverted behavior of [Shift] key when copying/pasting text or importing/exporting book.
  - Formatting is now always kept by default, unless [Shift] key is held.
- Insert Empty Page and Remove Page keyboard shortcuts can be changed now.
  - Default binds have changed as well.

## 1.1.16 - 2026-05-19
- Improved handling of complex Unicode symbols.

## 1.1.14 - 2026-05-03
- Added pt_BR translation.

## 1.1.13 - 2026-02-06
- Added automatic saving of changes (every 60 seconds).
- Fixed entered text disappearing from the book when game window is resized.

## 1.1.12 - 2026-01-09
- Fixed signing screen not checking for title length properly, causing disconnect/crash. 

## 1.1.11.1 - 2025-12-04
- Removed unused item translations (from the old book coloring system) (forgot to do that in previous release)

## 1.1.11 - 2025-12-04
- Removed unused book models and textures (from the old book coloring system) 

## 1.1.10 - 2025-12-03
- Added translations to config options
- Removed `chiseled_bookshelf_colors` config option as it was not working properly and not needed anyway as it is disabled by removing a resourcepack. 

## 1.1.9 - 2025-10-19
- Changed how support to show actual book colors in modded bookshelves is added. [Wiki Page](https://github.com/mortuusars/Scholar/wiki/Actual-book-colors-in-Chiseled-Bookshelves-customization)
  - Only resourcepack is now required, changes from Scholar's side are not needed anymore.
- Enabled the use of in-game configuration screens (accessed through the mod list).

## 1.1.8 - 2025-10-13
- Fixed pressing "Take Book" on a lectern not saving last edits.
- Fixed page contents not saving in some cases.

## 1.1.7 - 2025-05-17
- [NeoForge] Re-enabled compat with Woodworks.

## 1.1.6 - 2025-04-16
- [NeoForge] Added Woodster compat for book colors in chiseled bookshelves.

## 1.1.5 - 2025-03-30
- [NeoForge] MCBV compat now works properly 

## 1.1.4 - 2025-03-23
- Colored Books and Chiseled Bookshelf book colors are now added using built-in resourcepacks.
  - You can disable these features easily now or increase their priority to fix mods or resourcepacks overriding the changes.

## 1.1.3 - 2025-03-22
- [Fabric] Added 'colored books in bookshelves' compat with `More Chiseled Bookshelf Variants`.
- Changed structure of `colored books in bookshelves` assets.  

## 1.1.2 - 2025-03-19
- Formatting hotkeys now use Ctrl as a modifier, instead of Alt.
  - Changed color hotkeys slightly.

## 1.21.1 - 1.1.1 - 2025-03-16
- Ported to 1.21.

## 1.1.1 - 2025-03-16
- Chiseled Bookshelf now displays colored books in their proper color (thanks Fuzss).
- Added Mexican Spanish translation (thanks TheLegendofSaram).

## 1.1.0 - 2025-03-13

- *Book Editing:*
  - Added better formatting. When part of a text is selected, formatting toolbar will be shown.
  - Added additional editing tools like **inserting/removing pages** and **importing/exporting books**. Accessed by pressing F1.
  - Added undo/redo. Ctrl+Z/Ctrl+Shift+Z.
  - Books placed on a **Lectern** can now be edited.

- **Book and Quill** is now colored like leather armor.
  - Colored books added by Scholar have been **disabled**. They are still in the game for the time being and can be used in hand to convert them to a correct item.

- Added in-world tooltip to Chiseled Bookshelf that shows what book is stored in the hovered slot.
- You can now select specific page when book is placed on a Lectern by clicking on a page number.
  - Allows controlling comparator output properly. *At least one instance where this was a problem is in **YUNG's Better Desert Temples** puzzle*.


- *Config:*
  - Has been refreshed completely.
    - All config settings were renamed.
    - Added settings related to book coloring, extra editing tools, etc.
    - Added missing config settings for signing screen font colors.
    - Removed no longer utilized settings.

## 1.0.0 - 2024-03-27
- Release