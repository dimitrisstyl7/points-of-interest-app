package dimstyl.pointsofinterest.ui.components.dialogs

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import dimstyl.pointsofinterest.R
import dimstyl.pointsofinterest.ui.components.OutlinedTextField
import dimstyl.pointsofinterest.ui.screens.main.LazyColumnType
import dimstyl.pointsofinterest.ui.screens.main.MainState
import dimstyl.pointsofinterest.ui.screens.main.MainViewModel
import dimstyl.pointsofinterest.ui.screens.main.PointOfInterestUiModel
import dimstyl.pointsofinterest.ui.theme.DialogCameraIconColor
import dimstyl.pointsofinterest.ui.theme.DialogConfirmButtonTextColor
import dimstyl.pointsofinterest.ui.theme.DialogContainerColor
import dimstyl.pointsofinterest.ui.theme.DialogDismissButtonTextColor
import dimstyl.pointsofinterest.ui.theme.DialogFavoriteIconColor
import dimstyl.pointsofinterest.ui.theme.DialogIconContentColor
import dimstyl.pointsofinterest.ui.theme.DialogTitleContentColor

@Composable
fun NewPlaceDialog(
    viewModel: MainViewModel,
    state: MainState,
    showSnackbar: (String, Boolean) -> Unit,
    showToast: (String, Int) -> Unit,
    createTempImageUri: () -> Uri,
    copyTempImageToPermanent: (Uri) -> Uri
) {
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (!success) {
                showToast("Couldn't take the photo. Please try again.", Toast.LENGTH_SHORT)
            }
        }
    )

    // Camera permission launcher
    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(
                permission = Manifest.permission.CAMERA,
                isGranted = isGranted
            )

            // If permission is granted, open camera
            if (isGranted) {
                val uri = createTempImageUri()
                val pointOfInterest = state.pointOfInterestUiModel.copy(photoUri = uri.toString())
                viewModel.setNewPointOfInterestUiModel(pointOfInterest)
                cameraLauncher.launch(uri)
            }
        }
    )

    // Define FocusRequester for each field
    val focusRequesterTitle = remember { FocusRequester() }
    val focusRequesterCategory = remember { FocusRequester() }
    val focusRequesterDescription = remember { FocusRequester() }
    val focusRequesterRating = remember { FocusRequester() }

    // Track whether the user has interacted with each required field.
    // Used to avoid showing validation errors (e.g., red outline) before user input.
    var titleTouched by remember { mutableStateOf(false) }
    var categoryTouched by remember { mutableStateOf(false) }
    var ratingTouched by remember { mutableStateOf(false) }


    AlertDialog(
        icon = { Icon(painter = painterResource(R.drawable.add_place), contentDescription = null) },
        title = { Text(text = "Your New Discovery") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Title text field
                item(key = LazyColumnType.TITLE, contentType = LazyColumnType.TITLE) {
                    val title = state.pointOfInterestUiModel.title

                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequesterTitle),
                        label = "Title *",
                        value = title,
                        onValueChange = {
                            titleTouched = true
                            val pointOfInterest = state.pointOfInterestUiModel.copy(title = it)
                            viewModel.setNewPointOfInterestUiModel(pointOfInterest)
                        },
                        isError = titleTouched && title.isBlank(),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterCategory.requestFocus(focusDirection = FocusDirection.Next)
                        })
                    )
                }
                // Category text field
                item(key = LazyColumnType.CATEGORY, contentType = LazyColumnType.CATEGORY) {
                    val category = state.pointOfInterestUiModel.category

                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequesterCategory),
                        label = "Category *",
                        value = category,
                        onValueChange = {
                            categoryTouched = true
                            val pointOfInterest = state.pointOfInterestUiModel.copy(category = it)
                            viewModel.setNewPointOfInterestUiModel(pointOfInterest)
                        },
                        isError = categoryTouched && category.isBlank(),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterRating.requestFocus(focusDirection = FocusDirection.Next)
                        })
                    )
                }
                // Rating text field
                item(key = LazyColumnType.RATING, contentType = LazyColumnType.RATING) {
                    val min = state.pointOfInterestUiModel.minRating
                    val max = state.pointOfInterestUiModel.maxRating
                    val rating = state.pointOfInterestUiModel.rating
                    val hasError: (String) -> Boolean = { it.isBlank() || it.toInt() !in min..max }

                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequesterRating),
                        label = "Rating (${min}–${max}) *",
                        value = rating,
                        onValueChange = {
                            ratingTouched = true
                            if (hasError(it)) {
                                viewModel.setNewPointOfInterestUiModel(
                                    state.pointOfInterestUiModel.copy(rating = "")
                                )
                            } else {
                                val pointOfInterest = state.pointOfInterestUiModel.copy(rating = it)
                                viewModel.setNewPointOfInterestUiModel(pointOfInterest)
                            }
                        },
                        isError = ratingTouched && hasError(rating),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterDescription.requestFocus(focusDirection = FocusDirection.Next)
                        })
                    )
                }
                // Description text field
                item(key = LazyColumnType.DESCRIPTION, contentType = LazyColumnType.DESCRIPTION) {
                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequesterDescription),
                        label = "Description",
                        value = state.pointOfInterestUiModel.description,
                        onValueChange = {
                            val pointOfInterest =
                                state.pointOfInterestUiModel.copy(description = it)
                            viewModel.setNewPointOfInterestUiModel(pointOfInterest)
                        },
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions()
                    )
                }
                // Photo & Favorite buttons
                item(
                    key = LazyColumnType.PHOTO_FAVORITES,
                    contentType = LazyColumnType.PHOTO_FAVORITES
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Photo button
                        IconButton(
                            onClick = {
                                cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                            },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = DialogCameraIconColor)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add_a_photo),
                                contentDescription = "Add a photo"
                            )
                        }
                        // Favorite button
                        val isFavorite = state.pointOfInterestUiModel.isFavorite
                        IconButton(
                            onClick = {
                                val pointOfInterest =
                                    state.pointOfInterestUiModel.copy(isFavorite = !isFavorite)
                                val message =
                                    if (!isFavorite) "Added to favorites" else "Removed from favorites"
                                viewModel.setNewPointOfInterestUiModel(pointOfInterest)
                                showSnackbar(message, true)
                            },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = DialogFavoriteIconColor)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Mark as favorite"
                            )
                        }
                    }
                }
                // TODO: Location
            }
        },
        containerColor = DialogContainerColor,
        titleContentColor = DialogTitleContentColor,
        textContentColor = Color.Unspecified,
        iconContentColor = DialogIconContentColor,
        onDismissRequest = {
            viewModel.setNewPointOfInterestUiModel(PointOfInterestUiModel())
            viewModel.showNewPlaceDialog(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (viewModel.validatePointOfInterestUiModel()) {
                        // Use a toast instead of a snackbar to ensure the message appears
                        // above the dialog and is immediately visible to the user.
                        showToast("Please fill in all required fields", Toast.LENGTH_LONG)
                    } else {
                        // Copy photo from cache to permanent storage
                        val uri = state.pointOfInterestUiModel.photoUri.toUri()
                        val newUri = copyTempImageToPermanent(uri).toString()

                        // Check if the copy was successful
                        if (newUri.isBlank()) {
                            showToast("Failed to save your photo", Toast.LENGTH_SHORT)
                            return@TextButton
                        }

                        // Save point of interest to database
                        val pointOfInterest = state.pointOfInterestUiModel.copy(photoUri = newUri)
                        viewModel.setNewPointOfInterestUiModel(pointOfInterest)
                        viewModel.savePointOfInterest()
                        viewModel.showNewPlaceDialog(false)

                        showSnackbar(
                            "\"${pointOfInterest.title}\" has been added to your places",
                            true
                        )
                    }
                },
                colors = ButtonDefaults.textButtonColors(contentColor = DialogConfirmButtonTextColor)
            ) { Text(text = "Add") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.setNewPointOfInterestUiModel(PointOfInterestUiModel())
                    viewModel.showNewPlaceDialog(false)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = DialogDismissButtonTextColor)
            ) { Text(text = "Cancel") }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    )
}