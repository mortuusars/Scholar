# Changelog

## 1.2.0 - UNRELEASED 

_New features:_
- **Book Reading Animation**
  - Opening Book and Quill or Written Book changes the user's pose and the book model in hand.
  - Book and Quill makes it a "writing" animation, adding a feather to the hand.
  - This is controlled by presence of a `scholar:book_open` component on the item.
  - Some humanoid mobs support this animation as well.
- **Literate Mobs**
  - Zombies, Husks and Skeletons have a 5% chance to spawn with a book in hand and will be reading it from time to time.
  - Book has a 50% chance to drop when the mob dies.
  - Book item is controlled by a `scholar:entities/literate_mob_book` loot-table.
- **Changeable Author**
  - Author field on the book signing screen is now editable.
  - And it even allows formatting.
- **Bookmark**
  - Books in hand have a small red button above the pages, clicking on it sets the bookmark on that page.
  - Next time the book is opened it will open on that page.
- **Exporting Signed Books**
  - Exported to the same **.txt** format that Book and Quills can import.
- **Lectern Book Tooltip**
  - Displays placed book when looking at a block. Same system as Chiseled Bookshelf tooltip.
- **Lectern Book Model**
  - Displays actual color of the book.
  - Added some shading on the pages under the ones that stick out.
- **Golden Book Skin**
  - Patreon supporters of **Gold** tier and above can change the book appearance to golden, by pressing a button in the book editing screen.
- Holding [Shift] when changing page now jumps to the start/end of the book.
  - It works in steps - first jump is to the first page with content on it, if already on it or past - jump to end.
- [Scroll Wheel] changes pages.
- Added sound to book opening
- Added sounds to formatting actions

_Changes:_
- Inverted behavior of [Shift] key when copying/pasting text or importing/exporting book - formatting is now included by default, and excluded if [Shift] key is held.
- Insert Empty Page and Remove Page keyboard shortcuts can be changed.
  - Default binds have changed as well.
- Toggle Extra Tools button icon changed to a pencil icon instead of a question mark.
  - It will also flash red until the player toggles it tools for the first time - to help new players discover the feature.
- Moved definition of book item colors from Chiseled Bookshelf `chiseled_bookshelf/item_colors` built-in resourcepack to Colored Books `book/item_colors` built-in resourcepack.
- Restructured config. Moved some config options from client to common for convenience.
- Added "requires_sneaking" config option for showing in-world tooltips.

_Fixes:_
- Fixed page numbers not being centered properly.
- Fixed signing screen buttons being slightly misaligned.
- Fixed Chiseled Bookshelf tooltip still displaying when the GUI is hidden.

## 1.1.16 - 2026-05-19
- Improved handling of complex Unicode symbols. 

## 1.1.15 - 2026-05-03
- Added pt_BR translation.

## 1.1.14 - 2026-03-09
- Fixed Written Book text after formatted section resetting to default style (white with shadow). 

## 1.1.13 - 2026-02-06
- Added automatic saving of changes (every 60 seconds).
- Fixed entered text disappearing from the book when game window is resized.

## 1.1.12 - 2026-01-09
- Fixed signing screen not checking for title length properly, causing disconnect/crash.

## 1.1.11 - 1.21.11
- Port to 1.21.11

## 1.1.11 - 1.21.10
- Port to 1.21.10

## 1.1.11 - 1.21.8
- Port to 1.21.8

## 1.1.11 - 1.21.5
- Port to 1.21.5

## 1.1.11 - 1.21.4
- Port to 1.21.4

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