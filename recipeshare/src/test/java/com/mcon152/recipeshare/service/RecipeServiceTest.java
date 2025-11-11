package com.mcon152.recipeshare.service;

import com.mcon152.recipeshare.Recipe;
import com.mcon152.recipeshare.repository.RecipeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Assignment: Implement all TODOs using Mockito features covered in class:
 *  - @Mock, @InjectMocks, @Captor, @ExtendWith(MockitoExtension.class)
 *  - Stubbing: thenReturn / thenAnswer / thenThrow
 *  - Verifications: verify(...), times/never/atLeast..., verifyNoMoreInteractions
 *  - InOrder (where meaningful)
 *  - Void stubbing: doNothing / doThrow (use deleteById for this)
 *  - Matchers: any(), eq(), argThat()
 *  - ArgumentCaptor
 *  - (Optional) Spy demo if you introduce a small helper in tests
 *
 * NOTE: This is a pure unit test. Do NOT start a Spring context.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeService (Mockito) — Assignment Skeleton")
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService; // CUT implements RecipeService

    @Captor
    private ArgumentCaptor<Recipe> recipeCaptor;

    // --- Helpers for sample data ---

    private Recipe newRecipeNoId() {
        return new Recipe(
                null,
                "Chocolate Cake",
                "Moist chocolate cake",
                "flour, eggs, cocoa",
                "mix, bake",
                8
        );
    }

    private Recipe savedRecipe(long id) {
        return new Recipe(
                id,
                "Chocolate Cake",
                "Moist chocolate cake",
                "flour, eggs, cocoa",
                "mix, bake",
                8
        );
    }

    // ------------------ addRecipe ------------------

    @Nested
    @DisplayName("addRecipe(Recipe)")
    class AddRecipe {

        @Test
        @DisplayName("returns saved entity (thenReturn) and calls repository.save once")
        void returnsSaved_andSavesOnce() {
            // TODO:
            // 1) when(recipeRepository.save(...)).thenReturn(savedRecipe(1L))
            // 2) call recipeService.addRecipe(newRecipeNoId())
            // 3) assert non-null id and fields
            // 4) verify(recipeRepository).save(any(Recipe.class)); verifyNoMoreInteractions(recipeRepository)

            //See code below as an example answer

            Recipe input = newRecipeNoId();
            Recipe saved = savedRecipe(1L);

            when(recipeRepository.save(any(Recipe.class))).thenReturn(saved);

            Recipe out = recipeService.addRecipe(input);
            assertEquals(1L, out.getId());
            assertEquals(saved, out);

            verify(recipeRepository).save(any(Recipe.class));
            verifyNoMoreInteractions(recipeRepository);
        }

        @Test
        @DisplayName("assigns ID dynamically (thenAnswer) and captures argument")
        void assignsId_thenAnswer_andCaptures() {
            // TODO:
            // 1) Use thenAnswer to return a new Recipe with id=1L, copying fields from arg
            // 2) capture the arg with ArgumentCaptor and assert title, id==null pre-save

            //See code below as an example answer

            when(recipeRepository.save(any(Recipe.class))).thenAnswer(inv -> {
                Recipe r = inv.getArgument(0);
                return new Recipe(1L, r.getTitle(), r.getDescription(),
                        r.getIngredients(), r.getInstructions(), r.getServings());
            });

            Recipe out = recipeService.addRecipe(newRecipeNoId());
            assertEquals(1L, out.getId());

            verify(recipeRepository).save(recipeCaptor.capture());
            Recipe sent = recipeCaptor.getValue();
            assertNull(sent.getId()); // before persistence
            assertEquals("Chocolate Cake", sent.getTitle());
        }

        @Test
        @DisplayName("propagates repository failure (thenThrow)")
        void propagatesRepositoryFailure() {
            // TODO:
            // when(recipeRepository.save(any())).thenThrow(new IllegalStateException("DB down"))
            // assertThrows on recipeService.addRecipe(...)

            when(recipeRepository.save(any()))
                    .thenThrow(new IllegalStateException("DB down"));

            assertThrows(IllegalStateException.class, ()
            -> recipeService.addRecipe(newRecipeNoId()));

            verify(recipeRepository).save(any());
            verifyNoMoreInteractions(recipeRepository);
        }
    }

    // ------------------ getAllRecipes ------------------

    @Nested
    @DisplayName("getAllRecipes()")
    class GetAllRecipes {

        @Test
        @DisplayName("returns list from repository")
        void returnsList() {
            // TODO:
            // when(recipeRepository.findAll()).thenReturn(List.of(...))
            // assert same size/content; verify(findAll)

            List<Recipe> data = List.of(savedRecipe(1L), savedRecipe(2L));
            when(recipeRepository.findAll()).thenReturn(data);

            List<Recipe> out = recipeService.getAllRecipes();

            assertEquals(2, out.size());
            assertEquals(1L, out.get(0).getId());
            assertEquals(2L, out.get(1).getId());

            verify(recipeRepository).findAll();
            verifyNoMoreInteractions(recipeRepository);

        }
    }

    // ------------------ getRecipeById ------------------

    @Nested
    @DisplayName("getRecipeById(long)")
    class GetById {

        @Test
        @DisplayName("returns Optional.present when found")
        void present() {
            // TODO: stub findById(1L)->Optional.of(savedRecipe(1L)), assert present

            when(recipeRepository.findById(1L)).thenReturn(Optional.of(savedRecipe(1L)));

            Optional<Recipe> out = recipeService.getRecipeById(1L);

            assertTrue(out.isPresent());
            assertEquals(1L, out.get().getId());
            verify(recipeRepository).findById(1L);
            verifyNoMoreInteractions(recipeRepository);
        }

        @Test
        @DisplayName("returns Optional.empty when missing")
        void empty() {
            // TODO: stub Optional.empty, assert empty

            when(recipeRepository.findById(9L)).thenReturn(Optional.empty());

            Optional<Recipe> out = recipeService.getRecipeById(9L);

            assertTrue(out.isEmpty());
            verify(recipeRepository).findById(9L);
            verifyNoMoreInteractions(recipeRepository);
        }
    }

    // ------------------ deleteRecipe ------------------

    @Nested
    @DisplayName("deleteRecipe(long)")
    class DeleteRecipe {

        @Test
        @DisplayName("returns true when entity existed")
        void returnsTrue_whenExists() {
            // TODO:
            // when(recipeRepository.existsById(id)).thenReturn(true)
            // doNothing().when(recipeRepository).deleteById(id)
            // assert true; verify order: existsById -> deleteById

            long id = 5L;
            when(recipeRepository.existsById(id)).thenReturn(true);
            doNothing().when(recipeRepository).deleteById(id);

            boolean result = recipeService.deleteRecipe(id);
            assertTrue(result);

            InOrder order = inOrder(recipeRepository);
            order.verify(recipeRepository).existsById(id);
            order.verify(recipeRepository).deleteById(id);
            order.verifyNoMoreInteractions();

        }

        @Test
        @DisplayName("returns false when missing (never deletes)")
        void returnsFalse_whenMissing() {
            // TODO: existsById -> false; assert false; verify deleteById never called

            long id = 6L;
            when(recipeRepository.existsById(id)).thenReturn(false);
            boolean result = recipeService.deleteRecipe(id);
            assertFalse(result);

            verify(recipeRepository).existsById(id);
            verify(recipeRepository, never()).deleteById(anyLong());
            verifyNoMoreInteractions(recipeRepository);
        }

        @Test
        @DisplayName("propagates delete error (doThrow)")
        void propagatesDeleteError() {
            // TODO: existsById -> true; doThrow(...) on deleteById; assertThrows

            long id = 7L;
            when(recipeRepository.existsById(id)).thenReturn(true);
            doThrow(new RuntimeException("constraint")).when(recipeRepository).deleteById(id);

            assertThrows(RuntimeException.class, () -> recipeService.deleteRecipe(id));

            InOrder order = inOrder(recipeRepository);
            order.verify(recipeRepository).existsById(id);
            order.verify(recipeRepository).deleteById(id);
            verifyNoMoreInteractions(recipeRepository);
        }
    }

    // ------------------ updateRecipe ------------------

    @Nested
    @DisplayName("updateRecipe(long, Recipe)")
    class UpdateRecipe {

        @Test
        @DisplayName("returns updated entity when exists")
        void returnsUpdated_whenExists() {
            // TODO:
            // findById -> present(existing)
            // save(...) -> updatedSaved
            // assert Optional.present & fields updated
            // capture arg and assert values

            long id = 11L;
            Recipe existing = savedRecipe(id);
            when(recipeRepository.findById(id)).thenReturn(Optional.of(existing));

            // Echo back what was saved
            when(recipeRepository.save(any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

            Recipe changes = new Recipe(null, "NEW TITLE", "NEW DESC", "NEW ING", "NEW INST", null);

            Optional<Recipe> out = recipeService.updateRecipe(id, changes);
            assertTrue(out.isPresent());
            assertEquals(id, out.get().getId());
            assertEquals("NEW TITLE", out.get().getTitle());
            assertEquals("NEW DESC", out.get().getDescription());
            assertEquals("NEW ING", out.get().getIngredients());
            assertEquals("NEW INST", out.get().getInstructions());

            // if the service doesn't modify servings, assert original value:
            assertEquals(existing.getServings(), out.get().getServings()); // 8

            verify(recipeRepository).save(recipeCaptor.capture());
            Recipe toSave = recipeCaptor.getValue();
            assertEquals(id, toSave.getId());
            assertEquals("NEW TITLE", toSave.getTitle());
            verifyNoMoreInteractions(recipeRepository);
         }

        @Test
        @DisplayName("returns empty when entity missing")
        void returnsEmpty_whenMissing() {
            // TODO: findById -> empty; assert Optional.empty; verify save never called

            long id = 12L;
            when(recipeRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Recipe> out = recipeService.updateRecipe(id, newRecipeNoId());
            assertTrue(out.isEmpty());

            verify(recipeRepository).findById(id);
            verify(recipeRepository, never()).save(any());
            verifyNoMoreInteractions(recipeRepository);
        }
    }

    // ------------------ patchRecipe ------------------

    @Nested
    @DisplayName("patchRecipe(long, Recipe)")
    class PatchRecipe {

        @Test
        @DisplayName("applies only non-null fields (argThat)")
        void appliesNonNullFields_only() {
            // TODO:
            // findById -> present(existing)
            // provide partial with only title set
            // repository.save returns the modified entity (use thenAnswer echo)
            // verify save(argThat(...)) to ensure unchanged fields remain as-is

            long id = 21L;
            Recipe existing = new Recipe(id, "OLD", "D1", "I1", "N1", 4);
            when(recipeRepository.findById(id)).thenReturn(Optional.of(existing));

            // save echoes back the argument
            when(recipeRepository.save(any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

            // partial patch: only title set
            Recipe patch = new Recipe(null, "NEW", null, null, null, null);

            Optional<Recipe> out= recipeService.patchRecipe(id, patch);
            assertTrue(out.isPresent());

            // Ensure unchanged fields remain same via argThat
            verify(recipeRepository).save(argThat(r ->
                    r.getId().equals(id) &&
                    "NEW".equals(r.getTitle()) &&
                    "D1".equals(r.getDescription()) &&
                    "I1".equals(r.getIngredients()) &&
                    "N1".equals(r.getInstructions()) &&
                    Integer.valueOf(4).equals(r.getServings())
            ));
            verify(recipeRepository).findById(id);
            verifyNoMoreInteractions(recipeRepository);
        }

        @Test
        @DisplayName("returns empty when entity missing")
        void returnsEmpty_whenMissing() {
            // TODO: findById -> empty; assert Optional.empty; verify save never called

            long id = 22L;
            when(recipeRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Recipe> out = recipeService.patchRecipe(id, new Recipe());
            assertTrue(out.isEmpty());

            verify(recipeRepository).findById(id);
            verify(recipeRepository, never()).save(any());
            verifyNoMoreInteractions(recipeRepository);
         }
    }

    // ------------------ extra practice ------------------

    @Nested
    @DisplayName("Advanced stubbing & verification")
    class Advanced {

        @Test
        @DisplayName("consecutive stubs on existsById (true, false)")
        void consecutiveStubs_existsById() {
            // TODO: when(existsById(1L)).thenReturn(true, false); verify two calls and no more

            long id = 30L;

            // --- 1st delete: exists = true -> perform delete -> returns true
            when(recipeRepository.existsById(id)).thenReturn(true);
            doNothing().when(recipeRepository).deleteById(id);

            boolean first = recipeService.deleteRecipe(id);
            assertTrue(first);

            InOrder order1 = inOrder(recipeRepository);
            order1.verify(recipeRepository).existsById(id);
            order1.verify(recipeRepository).deleteById(id);
            order1.verifyNoMoreInteractions();

            // Clear interactions before the second scenario (canvas tip)
            clearInvocations(recipeRepository);

            // --- 2nd delete: exists=false -> no delete -> returns false
            when(recipeRepository.existsById(id)).thenReturn(false);

            boolean second = recipeService.deleteRecipe(id);
            assertFalse(second);

            InOrder order2 = inOrder(recipeRepository);
            order2.verify(recipeRepository).existsById(id);
            order2.verifyNoMoreInteractions();
         }
    }
}
