package com.codingwithmitch.food2forkcompose.presentation.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.codingwithmitch.food2forkcompose.presentation.BaseApplication
import com.codingwithmitch.food2forkcompose.presentation.components.LoadingRecipeShimmer
import com.codingwithmitch.food2forkcompose.presentation.components.RecipeView
import com.codingwithmitch.food2forkcompose.presentation.components.util.SnackbarController
import com.codingwithmitch.food2forkcompose.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

const val IMAGE_HEIGHT = 260

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeFragment: Fragment() {

    @Inject
    lateinit var application: BaseApplication

    private val snackbarController = SnackbarController(lifecycleScope)

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt("recipeId")?.let { recipeId ->
            viewModel.onTriggerEvent(RecipeEvent.GetRecipeEvent(recipeId))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply{
            setContent {

                val loading = viewModel.loading.value

                val recipe = viewModel.recipe.value

                val scaffoldState = rememberScaffoldState()

                AppTheme(
                    displayProgressBar = loading,
                    scaffoldState = scaffoldState,
                    darkTheme = application.isDark.value,
                ){
                    Scaffold(
                        scaffoldState = scaffoldState,
                        snackbarHost = {
                            scaffoldState.snackbarHostState
                        }
                    ) {
                        Box (
                            modifier = Modifier.fillMaxSize()
                        ){
                            if (loading && recipe == null) LoadingRecipeShimmer(imageHeight = IMAGE_HEIGHT.dp)
                            else recipe?.let {
                                if(it.id == 1) { // force an error to demo snackbar
                                    snackbarController.getScope().launch {
                                        snackbarController.showSnackbar(
                                            scaffoldState = scaffoldState,
                                            message = "An error occurred with this recipe",
                                            actionLabel = "Ok"
                                        )
                                    }
                                }
                                else{
                                    RecipeView(
                                        recipe = it,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}








