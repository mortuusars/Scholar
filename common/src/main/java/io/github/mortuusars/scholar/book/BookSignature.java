package io.github.mortuusars.scholar.book;

import java.util.Optional;

public record BookSignature(String title, Optional<String> customAuthor) {
}
