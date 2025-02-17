package org.oppia.android.domain.classify.rules.ratioinput

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.BindsInstance
import dagger.Component
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.oppia.android.app.model.WrittenTranslationContext
import org.oppia.android.domain.classify.InteractionObjectTestBuilder
import org.oppia.android.domain.classify.RuleClassifier
import org.oppia.android.testing.assertThrows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import javax.inject.Inject
import javax.inject.Singleton

/** Tests for [RatioInputHasNumberOfTermsEqualToClassifierProvider]. */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(manifest = Config.NONE)
class RatioInputHasNumberOfTermsEqualToClassifierProviderTest {

  private val NON_NEGATIVE_VALUE_TEST_3 =
    InteractionObjectTestBuilder.createNonNegativeInt(
      value = 3
    )
  private val NON_NEGATIVE_VALUE_TEST_4 =
    InteractionObjectTestBuilder.createNonNegativeInt(
      value = 4
    )
  private val RATIO_VALUE_TEST_1_2_3 =
    InteractionObjectTestBuilder.createRatio(
      listOf(1, 2, 3)
    )

  @Inject
  internal lateinit var ratioInputHasNumberOfTermsEqualToClassifierProvider:
    RatioInputHasNumberOfTermsEqualToClassifierProvider

  private val hasNumberOfTermsEqualToClassifierProvider: RuleClassifier by lazy {
    ratioInputHasNumberOfTermsEqualToClassifierProvider.createRuleClassifier()
  }

  @Before
  fun setUp() {
    setUpTestApplicationComponent()
  }

  @Test
  fun testAnswer_withThreeTerms_expected3_matchesCorrectly() {
    val inputs = mapOf("y" to NON_NEGATIVE_VALUE_TEST_3)

    val matches =
      hasNumberOfTermsEqualToClassifierProvider.matches(
        answer = RATIO_VALUE_TEST_1_2_3,
        inputs = inputs,
        writtenTranslationContext = WrittenTranslationContext.getDefaultInstance()
      )

    assertThat(matches).isTrue()
  }

  @Test
  fun testAnswer_withThreeTerms_expectedFour_doesNotMatch() {
    val inputs = mapOf("y" to NON_NEGATIVE_VALUE_TEST_4)

    val matches =
      hasNumberOfTermsEqualToClassifierProvider.matches(
        answer = RATIO_VALUE_TEST_1_2_3,
        inputs = inputs,
        writtenTranslationContext = WrittenTranslationContext.getDefaultInstance()
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testAnswer_nonNegativeInput_inputWithIncorrectType_throwsException() {
    val inputs = mapOf("y" to RATIO_VALUE_TEST_1_2_3)

    val exception = assertThrows(IllegalStateException::class) {
      hasNumberOfTermsEqualToClassifierProvider.matches(
        answer = RATIO_VALUE_TEST_1_2_3,
        inputs = inputs,
        writtenTranslationContext = WrittenTranslationContext.getDefaultInstance()
      )
    }

    assertThat(exception)
      .hasMessageThat()
      .contains(
        "Expected input value to be of type NON_NEGATIVE_INT not RATIO_EXPRESSION"
      )
  }

  @Test
  fun testAnswer_testRatio_missingInputY_throwsException() {
    val inputs = mapOf("x" to NON_NEGATIVE_VALUE_TEST_4)

    val exception = assertThrows(IllegalStateException::class) {
      hasNumberOfTermsEqualToClassifierProvider.matches(
        answer = RATIO_VALUE_TEST_1_2_3,
        inputs = inputs,
        writtenTranslationContext = WrittenTranslationContext.getDefaultInstance()
      )
    }

    assertThat(exception)
      .hasMessageThat()
      .contains("Expected classifier inputs to contain parameter with name 'y' but had: [x]")
  }

  private fun setUpTestApplicationComponent() {
    DaggerRatioInputHasNumberOfTermsEqualToClassifierProviderTest_TestApplicationComponent
      .builder()
      .setApplication(ApplicationProvider.getApplicationContext()).build().inject(this)
  }

  // TODO(#89): Move this to a common test application component.
  @Singleton
  @Component(modules = [])
  interface TestApplicationComponent {
    @Component.Builder
    interface Builder {
      @BindsInstance
      fun setApplication(application: Application): Builder

      fun build(): TestApplicationComponent
    }

    fun inject(test: RatioInputHasNumberOfTermsEqualToClassifierProviderTest)
  }
}
