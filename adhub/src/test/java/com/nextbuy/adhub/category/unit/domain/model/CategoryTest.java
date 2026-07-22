package com.nextbuy.adhub.category.unit.domain.model;

import com.nextbuy.adhub.category.domain.exception.CategoryDomainException;
import com.nextbuy.adhub.category.domain.model.Category;
import com.nextbuy.adhub.category.domain.model.CategoryId;
import com.nextbuy.adhub.support.category.fixtures.Categories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static com.nextbuy.adhub.support.category.fixtures.Categories.TEST_CATEGORY_ID;
import static com.nextbuy.adhub.support.category.fixtures.Categories.TEST_OTHER_CATEGORY_ID;
import static com.nextbuy.adhub.support.category.fixtures.Categories.TEST_PARENT_CATEGORY_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:CategoryTest")
public class CategoryTest {

    @Test
    @DisplayName("It should create a root category when name, slug, and no parent are valid.")
    void create_CreateRootCategory_When_AllFieldsValid() {
        // Given
        String name = "Electronics";
        String slug = "electronics";

        // When
        Category category = Categories.withNameAndSlug(name, slug);

        // Then
        assertThat(category.getId().isAssigned()).isFalse();
        assertThat(category.getName()).isEqualTo(name);
        assertThat(category.getSlug()).isEqualTo(slug);
        assertThat(category.getParentId()).isNull();
    }

    @Test
    @DisplayName("It should create a child category when a parent id is assigned.")
    void create_CreateChildCategory_When_ParentIsAssigned() {
        // Given
        CategoryId parentId = CategoryId.of(Categories.TEST_PARENT_CATEGORY_ID);

        // When
        Category category = Categories.withParent(parentId);

        // Then
        assertThat(category.getParentId()).isEqualTo(parentId);
        assertThat(category.getName()).isNotBlank();
        assertThat(category.getSlug()).isNotBlank();
    }

    @Test
    @DisplayName("It should reject creation when the name is blank.")
    void create_Throw_When_NameIsBlank() {
        assertThatThrownBy(() -> Categories.withName("  "))
                .isInstanceOf(CategoryDomainException.FieldIsRequired.class)
                .hasMessage("Category 'name' field is required");
    }

    @Test
    @DisplayName("It should reject creation when the name is missing.")
    void create_Throw_When_NameIsNull() {
        assertThatThrownBy(() -> Categories.withName(null))
                .isInstanceOf(CategoryDomainException.FieldIsRequired.class)
                .hasMessage("Category 'name' field is required");
    }

    @Test
    @DisplayName("It should reject creation when the slug is blank.")
    void create_Throw_When_SlugIsBlank() {
        assertThatThrownBy(() -> Categories.withSlug("  "))
                .isInstanceOf(CategoryDomainException.FieldIsRequired.class)
                .hasMessage("Category 'slug' field is required");
    }

    @Test
    @DisplayName("It should reject creation when the slug is missing.")
    void create_Throw_When_SlugIsNull() {
        assertThatThrownBy(() -> Categories.withSlug(null))
                .isInstanceOf(CategoryDomainException.FieldIsRequired.class)
                .hasMessage("Category 'slug' field is required");
    }

    @Test
    @DisplayName("It should rehydrate a category with all persisted fields when reconstruct is called.")
    void reconstruct_RestoreAllFields_When_Reconstructed() {
        // Given
        CategoryId id = CategoryId.of(TEST_OTHER_CATEGORY_ID);
        CategoryId parentId = CategoryId.of(TEST_PARENT_CATEGORY_ID);

        // When
        Category category = Categories.reconstructFromPersisted(
                TEST_OTHER_CATEGORY_ID,
                "Electronics",
                "electronics",
                parentId
        );

        // Then
        assertThat(category.getId()).isEqualTo(id);
        assertThat(category.getName()).isEqualTo("Electronics");
        assertThat(category.getSlug()).isEqualTo("electronics");
        assertThat(category.getParentId()).isEqualTo(parentId);
    }

    @Test
    @DisplayName("It should preserve a null parent when reconstructed from persistence.")
    void reconstruct_PreserveNullParent_When_Reconstructed() {
        Category category = Categories.reconstructFromPersisted(
                TEST_CATEGORY_ID, "Electronics", "electronics", null
        );

        assertThat(category.getParentId()).isNull();
    }

    @Test
    @DisplayName("It should preserve name and slug separately when reconstructed.")
    void reconstruct_PreserveNameAndSlugSeparately_When_Reconstructed() {
        Category category = Categories.reconstructFromPersisted(
                TEST_CATEGORY_ID,
                "Display Name",
                "display-name",
                null
        );

        assertThat(category.getName()).isEqualTo("Display Name");
        assertThat(category.getSlug()).isEqualTo("display-name");
    }

    @Test
    @DisplayName("It should trim name and slug on create.")
    void create_TrimNameAndSlug_When_Created() {
        Category category = Categories.withNameAndSlug("  Electronics  ", "  electronics  ");

        assertThat(category.getName()).isEqualTo("Electronics");
        assertThat(category.getSlug()).isEqualTo("electronics");
    }

    @Test
    @DisplayName("It should trim the name on rename.")
    void rename_TrimName_When_Renamed() {
        Category category = Categories.withName("Old name");

        category.rename("  New name  ");

        assertThat(category.getName()).isEqualTo("New name");
    }

    @Test
    @DisplayName("It should rename the category when the new name is valid.")
    void rename_RenameCategory_When_NewNameIsValid() {
        // Given
        Category category = Categories.withName("Old name");

        // When
        category.rename("New name");

        // Then
        assertThat(category.getName()).isEqualTo("New name");
    }

    @Test
    @DisplayName("It should reject rename when the new name is blank.")
    void rename_Throw_When_RenameToBlankName() {
        Category category = Categories.random();

        assertThatThrownBy(() -> category.rename("  "))
                .isInstanceOf(CategoryDomainException.FieldIsRequired.class)
                .hasMessage("Category 'name' field is required");
    }

    @Test
    @DisplayName("It should reject rename when the new name is missing.")
    void rename_Throw_When_RenameToNullName() {
        Category category = Categories.random();

        assertThatThrownBy(() -> category.rename(null))
                .isInstanceOf(CategoryDomainException.FieldIsRequired.class)
                .hasMessage("Category 'name' field is required");
    }

    @Test
    @DisplayName("It should assign a parent when the new parent id is valid.")
    void assignParent_AssignParent_When_NewParentIsValid() {
        // Given
        Category category = Categories.withoutParent();
        CategoryId parentId = CategoryId.of(TEST_PARENT_CATEGORY_ID);

        // When
        category.assignParent(parentId);

        // Then
        assertThat(category.getParentId()).isEqualTo(parentId);
    }

    @Test
    @DisplayName("It should clear the parent when assignParent is called with null.")
    void assignParent_ClearParent_When_AssignParentWithNull() {
        // Given
        Category category = Categories.withParent(CategoryId.of(TEST_PARENT_CATEGORY_ID));

        // When
        category.assignParent(null);

        // Then
        assertThat(category.getParentId()).isNull();
    }

    @Test
    @DisplayName("It should reject assigning a category as its own parent.")
    void assignParent_Throw_When_AssigningSelfAsParent() {
        // Given
        Category category = Categories.reconstructFromPersisted(
                TEST_OTHER_CATEGORY_ID, "Electronics", "electronics", null
        );

        // When / Then
        assertThatThrownBy(() -> category.assignParent(CategoryId.of(TEST_OTHER_CATEGORY_ID)))
                .isInstanceOf(CategoryDomainException.CategoryCannotBeItsOwnParentException.class)
                .hasMessage("A category cannot be its own parent with id %s.".formatted(TEST_OTHER_CATEGORY_ID));
    }

    @Test
    @DisplayName("It should allow rename and parent assignment after reconstruction.")
    void reconstruct_AllowRenameAndAssignParent_When_Reconstructed() {
        // Given
        Category category = Categories.reconstructFromPersisted(
                TEST_CATEGORY_ID, "Old", "old", null
        );

        // When
        category.rename("New");
        category.assignParent(CategoryId.of(TEST_OTHER_CATEGORY_ID));

        // Then
        assertThat(category.getName()).isEqualTo("New");
        assertThat(category.getParentId()).isEqualTo(CategoryId.of(TEST_OTHER_CATEGORY_ID));
    }
}
