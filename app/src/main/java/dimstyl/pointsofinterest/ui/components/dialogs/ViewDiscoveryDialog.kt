package dimstyl.pointsofinterest.ui.components.dialogs

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dimstyl.pointsofinterest.R
import dimstyl.pointsofinterest.ui.components.ButtonCircularProgressIndicator
import dimstyl.pointsofinterest.ui.components.OutlinedTextField
import dimstyl.pointsofinterest.ui.models.PointOfInterestUiModel
import dimstyl.pointsofinterest.ui.screens.main.LazyColumnType
import dimstyl.pointsofinterest.ui.screens.main.MainState
import dimstyl.pointsofinterest.ui.screens.main.MainViewModel
import dimstyl.pointsofinterest.ui.theme.DialogCameraIconColor
import dimstyl.pointsofinterest.ui.theme.DialogConfirmButtonTextColor
import dimstyl.pointsofinterest.ui.theme.DialogContainerColor
import dimstyl.pointsofinterest.ui.theme.DialogDeleteIconColor
import dimstyl.pointsofinterest.ui.theme.DialogDeletePhotoButtonColor
import dimstyl.pointsofinterest.ui.theme.DialogDismissButtonTextColor
import dimstyl.pointsofinterest.ui.theme.DialogFavoriteIconColor
import dimstyl.pointsofinterest.ui.theme.DialogIconContentColor
import dimstyl.pointsofinterest.ui.theme.DialogShowHidePhotoButtonColor
import dimstyl.pointsofinterest.ui.theme.DialogTitleContentColor

@Composable
fun ViewDiscoveryDialog(
    viewModel: MainViewModel,
    state: MainState,
    showSnackbar: (String, Boolean) -> Unit,
    showToast: (String, Int) -> Unit,
    createTempPhotoUri: () -> Uri,
    copyTempPhotoToPermanent: (Uri) -> Uri
) {
    val context = LocalContext.current
    val draftPointOfInterestUiModel = state.draftPointOfInterestUiModel

    var photoUri by rememberSaveable { mutableStateOf<Uri?>(draftPointOfInterestUiModel.photoUri?.toUri()) }
    var photoInPreview by rememberSaveable { mutableStateOf(false) }
    var photoCaptured by rememberSaveable { mutableStateOf(draftPointOfInterestUiModel.photoUri != null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            photoCaptured = success
            if (!success) {
                // Restore initial photoUri if the camera was dismissed or failed.
                photoUri = draftPointOfInterestUiModel.photoUri?.toUri()
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

            // If permission is not granted, return.
            if (!isGranted) return@rememberLauncherForActivityResult

            /*
            If user already captured a photo show toast message,
            otherwise proceed with photo capture.
            */
            if (photoUri == null) {
                val uri = createTempPhotoUri()
                photoUri = uri
                cameraLauncher.launch(uri)
            } else {
                showToast(
                    "Only one photo allowed. Delete the current one to add a new photo.",
                    Toast.LENGTH_LONG
                )
            }
        }
    )

    // Define FocusRequester for each field
    val focusRequesterTitle = remember { FocusRequester() }
    val focusRequesterCategory = remember { FocusRequester() }
    val focusRequesterDescription = remember { FocusRequester() }
    val focusRequesterRating = remember { FocusRequester() }

    /*
    Track whether the user has interacted with each required field.
    Used to avoid showing validation errors (e.g., red outline) before user input.
    */
    var titleTouched by remember { mutableStateOf(false) }
    var categoryTouched by remember { mutableStateOf(false) }
    var ratingTouched by remember { mutableStateOf(false) }

    AlertDialog(
        icon = { Icon(imageVector = Icons.Default.Place, contentDescription = null) },
        title = { Text(text = "Your Discovery") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Title text field
                item(key = LazyColumnType.TITLE, contentType = LazyColumnType.TITLE) {
                    val title = draftPointOfInterestUiModel.title
                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequesterTitle),
                        label = "Title *",
                        value = title,
                        onValueChange = {
                            titleTouched = true
                            val pointOfInterest = draftPointOfInterestUiModel.copy(title = it)
                            viewModel.setDraftPointOfInterestUiModel(pointOfInterest)
                        },
                        isError = titleTouched && title.isBlank(),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterCategory.requestFocus(focusDirection = FocusDirection.Next)
                        })
                    )
                }
                // Category text field
                item(key = LazyColumnType.CATEGORY, contentType = LazyColumnType.CATEGORY) {
                    val category = draftPointOfInterestUiModel.category
                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequesterCategory),
                        label = "Category *",
                        value = category,
                        onValueChange = {
                            categoryTouched = true
                            val pointOfInterest = draftPointOfInterestUiModel.copy(category = it)
                            viewModel.setDraftPointOfInterestUiModel(pointOfInterest)
                        },
                        isError = categoryTouched && category.isBlank(),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterRating.requestFocus(focusDirection = FocusDirection.Next)
                        })
                    )
                }
                // Rating text field
                item(key = LazyColumnType.RATING, contentType = LazyColumnType.RATING) {
                    val min = draftPointOfInterestUiModel.minRating
                    val max = draftPointOfInterestUiModel.maxRating
                    val rating = draftPointOfInterestUiModel.rating
                    val hasError: (String) -> Boolean = { it.isBlank() || it.toInt() !in min..max }

                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequesterRating),
                        label = "Rating (${min}–${max}) *",
                        value = rating,
                        onValueChange = {
                            ratingTouched = true
                            if (hasError(it)) {
                                viewModel.setDraftPointOfInterestUiModel(
                                    draftPointOfInterestUiModel.copy(rating = "")
                                )
                            } else {
                                val pointOfInterest = draftPointOfInterestUiModel.copy(rating = it)
                                viewModel.setDraftPointOfInterestUiModel(pointOfInterest)
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
                        value = draftPointOfInterestUiModel.description,
                        onValueChange = {
                            val pointOfInterest =
                                draftPointOfInterestUiModel.copy(description = it)
                            viewModel.setDraftPointOfInterestUiModel(pointOfInterest)
                        },
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions()
                    )
                }
                // Delete discovery, photo capture, and favorite buttons
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
                        // Delete discovery button
                        val discoveryTitle = state.pointOfInterestUiModel.title
                        IconButton(
                            onClick = {
                                viewModel.deletePointOfInterest(
                                    context = context,
                                    showSnackbar = { message, shortDuration ->
                                        showSnackbar(message, shortDuration)
                                    })
                            },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = DialogDeleteIconColor)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete discovery \"$discoveryTitle\""
                            )
                        }
                        // Photo capture button
                        IconButton(
                            onClick = {
                                cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                            },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = DialogCameraIconColor)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.capture_a_photo),
                                contentDescription = "Add a photo"
                            )
                        }
                        // Favorite button
                        val isFavorite = draftPointOfInterestUiModel.isFavorite
                        IconButton(
                            onClick = {
                                val pointOfInterest =
                                    draftPointOfInterestUiModel.copy(isFavorite = !isFavorite)
                                val message =
                                    if (!isFavorite) "Added to favorites" else "Removed from favorites"
                                viewModel.setDraftPointOfInterestUiModel(pointOfInterest)
                                showToast(message, Toast.LENGTH_SHORT)
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
                // Photo preview
                item(
                    key = LazyColumnType.PHOTO_PREVIEW,
                    contentType = LazyColumnType.PHOTO_PREVIEW
                ) {
                    // If photoUri is null or photoCaptured == false, return (no captured photo).
                    if (photoUri == null || !photoCaptured) return@item

                    Spacer(Modifier.height(8.dp))

                    if (photoInPreview) {
                        AsyncImage(
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photoUri)
                                .crossfade(true)
                                .build(),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )
                    }

                    // View-Hide/Delete photo buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // View-Hide photo button
                        TextButton(
                            onClick = { photoInPreview = !photoInPreview },
                            colors = ButtonDefaults.textButtonColors(contentColor = DialogShowHidePhotoButtonColor)
                        ) {
                            val text = if (!photoInPreview) "View Photo" else "Hide Photo"
                            Text(text = text)
                        }
                        // Delete photo button
                        TextButton(
                            onClick = {
                                photoUri = null
                                photoInPreview = false
                                photoCaptured = false
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = DialogDeletePhotoButtonColor)
                        ) {
                            Text(text = "Delete Photo")
                        }
                    }
                }
            }
        },
        containerColor = DialogContainerColor,
        titleContentColor = DialogTitleContentColor,
        textContentColor = Color.Unspecified,
        iconContentColor = DialogIconContentColor,
        onDismissRequest = {
            viewModel.setPointOfInterestUiModel(PointOfInterestUiModel())
            viewModel.showViewDiscoveryDialog(false)
        },
        confirmButton = {
            if (state.processingPointOfInterest) {
                ButtonCircularProgressIndicator()
            } else {
                TextButton(
                    onClick = {
                        viewModel.updatePointOfInterest(
                            context = context,
                            photoUri = photoUri,
                            copyTempPhotoToPermanent = copyTempPhotoToPermanent,
                            showToast = { message, duration -> showToast(message, duration) },
                            // @formatter:off
                            showSnackbar = { message, shortDuration -> showSnackbar(message, shortDuration) }
                            // @formatter:on
                        )
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = DialogConfirmButtonTextColor)
                ) { Text(text = "Save") }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.setPointOfInterestUiModel(PointOfInterestUiModel())
                    viewModel.showViewDiscoveryDialog(false)
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
