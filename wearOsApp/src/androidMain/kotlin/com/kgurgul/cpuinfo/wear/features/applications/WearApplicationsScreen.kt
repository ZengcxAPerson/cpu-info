@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHorologistApi::class,
    ExperimentalWearMaterialApi::class
)

package com.kgurgul.cpuinfo.wear.features.applications

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.RevealValue
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.rememberRevealState
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.SwipeToRevealChip
import androidx.wear.compose.material.SwipeToRevealDefaults
import androidx.wear.compose.material.SwipeToRevealPrimaryAction
import androidx.wear.compose.material.SwipeToRevealSecondaryAction
import androidx.wear.compose.material.Text
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.features.applications.ApplicationsViewModel
import com.kgurgul.cpuinfo.features.applications.registerUninstallListener
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.applications
import com.kgurgul.cpuinfo.shared.apps_uninstall
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearApplicationsScreen(
    viewModel: ApplicationsViewModel = koinViewModel(),
) {
    registerUninstallListener(
        onRefresh = viewModel::onRefreshApplications,
    )
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearApplicationsScreen(
        uiState = uiState,
        onAppClicked = viewModel::onApplicationClicked,
        onRefreshApplications = viewModel::onRefreshApplications,
        onSnackbarDismissed = viewModel::onSnackbarDismissed,
        onAppUninstallClicked = viewModel::onAppUninstallClicked,
        onAppSettingsClicked = viewModel::onAppSettingsClicked,
        onSystemAppsSwitched = viewModel::onSystemAppsSwitched,
        onSortOrderChange = viewModel::onSortOrderChange,
    )
}

@Composable
fun WearApplicationsScreen(
    uiState: ApplicationsViewModel.UiState,
    onAppClicked: (packageName: String) -> Unit,
    onRefreshApplications: () -> Unit,
    onSnackbarDismissed: () -> Unit,
    onAppUninstallClicked: (id: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
    onSystemAppsSwitched: (enabled: Boolean) -> Unit,
    onSortOrderChange: (ascending: Boolean) -> Unit,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )
    ScreenScaffold(scrollState = columnState) {
        CpuPullToRefreshBox(
            isRefreshing = uiState.isLoading,
            enabled = false,
            onRefresh = { onRefreshApplications() },
            modifier = Modifier
                .fillMaxSize(),
        ) {
            ScalingLazyColumn(
                columnState = columnState
            ) {
                item {
                    ResponsiveListHeader(contentPadding = firstItemPadding()) {
                        Text(
                            text = stringResource(Res.string.applications),
                            color = MaterialTheme.colors.onBackground,
                        )
                    }
                }
                items(
                    items = uiState.applications,
                    key = { item -> item.packageName },
                ) { item ->
                    val revealState = rememberRevealState()
                    val coroutineScope = rememberCoroutineScope()
                    SwipeToRevealChip(
                        revealState = revealState,
                        modifier = Modifier
                            .fillMaxWidth()
                        //      .edgeSwipeToDismiss(swipeToDismissBoxState)
                        /*.semantics {
                        customActions = listOf(
                                CustomAccessibilityAction("Delete") {
                                    *//* Add the primary action click handler here *//*
                                            true
                                        },
                                        CustomAccessibilityAction("More Options") {
                                            *//* Add the secondary click handler here *//*
                                            true
                                        }
                                    )
                            }*/,
                        primaryAction = {
                            SwipeToRevealPrimaryAction(
                                revealState = revealState,
                                icon = {
                                    Icon(
                                        imageVector = SwipeToRevealDefaults.Delete,
                                        contentDescription = stringResource(
                                            Res.string.apps_uninstall
                                        )
                                    )
                                },
                                label = { Text(stringResource(Res.string.apps_uninstall)) },
                                onClick = { onAppUninstallClicked(item.packageName) },
                            )
                        },
                        secondaryAction = {
                            SwipeToRevealSecondaryAction(
                                revealState = revealState,
                                onClick = { onAppSettingsClicked(item.packageName) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = stringResource(Res.string.settings)
                                )
                            }
                        },
                        onFullSwipe = {
                            onAppUninstallClicked(item.packageName)
                            coroutineScope.launch {
                                revealState.animateTo(RevealValue.Covered)
                            }
                        }
                    ) {
                        WearCpuChip(
                            modifier = Modifier.fillMaxWidth(),
                            label = item.name,
                            secondaryLabel = item.packageName,
                            icon = {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalPlatformContext.current)
                                        .data(item.appIconUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .size(ChipDefaults.IconSize)
                                        .wrapContentSize(align = Alignment.Center)
                                )
                            }
                        )
                    }

                    /* val isRevealed by remember {
                         derivedStateOf { revealedCardId == item.packageName }
                     }
                     DraggableBox(
                         isRevealed = isRevealed,
                         onExpand = { revealedCardId = item.packageName },
                         onCollapse = {
                             if (revealedCardId == item.packageName) {
                                 revealedCardId = null
                             }
                         },
                         actionRowOffset = with(density) { rowActionIconSize.toPx() * 2 },
                         actionRow = {
                             Row {
                                 IconButton(
                                     modifier = Modifier.requiredSize(rowActionIconSize),
                                     onClick = { onAppSettingsClicked(item.packageName) },
                                 ) {
                                     Icon(
                                         painter = painterResource(Res.drawable.ic_settings),
                                         tint = MaterialTheme.colorScheme.onBackground,
                                         contentDescription =,
                                     )
                                 }
                                 IconButton(
                                     modifier = Modifier.requiredSize(rowActionIconSize),
                                     onClick = { onAppUninstallClicked(item.packageName) },
                                 ) {
                                     Icon(
                                         painter = painterResource(Res.drawable.ic_thrash),
                                         tint = MaterialTheme.colorScheme.onBackground,
                                         contentDescription = null,
                                     )
                                 }
                             }
                         },
                         content = {
                             ApplicationItem(
                                 appData = item,
                                 onAppClicked = onAppClicked,
                                 onNativeLibsClicked = onNativeLibsClicked,
                             )
                         },
                         modifier = Modifier.animateItem(),
                     )
                     if (index < appList.lastIndex) {
                         CpuDivider(
                             modifier = Modifier.padding(horizontal = spacingSmall),
                         )
                     }*/
                }
            }
        }
    }


    /*val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val snackbarMessageString = uiState.snackbarMessage?.let { stringResource(it) }
    LaunchedEffect(snackbarMessageString) {
        scope.launch {
            if (snackbarMessageString != null) {
                val result = snackbarHostState.showSnackbar(snackbarMessageString)
                if (result == SnackbarResult.Dismissed) {
                    onSnackbarDismissed()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                withSystemApps = uiState.withSystemApps,
                onSystemAppsSwitched = onSystemAppsSwitched,
                isSortAscending = uiState.isSortAscending,
                onSortOrderChange = onSortOrderChange,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                CpuSnackbar(data)
            }
        },
    ) { innerPaddingModifier ->
        CpuPullToRefreshBox(
            isRefreshing = uiState.isLoading,
            enabled = false,
            onRefresh = { onRefreshApplications() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingModifier),
        ) {
            ApplicationsList(
                appList = uiState.applications,
                onAppClicked = onAppClicked,
                onAppUninstallClicked = onAppUninstallClicked,
                onAppSettingsClicked = onAppSettingsClicked,
                onNativeLibsClicked = onNativeLibsClicked,
            )
        }
    }*/
}

/*@Composable
private fun TopBar(
    withSystemApps: Boolean,
    onSystemAppsSwitched: (enabled: Boolean) -> Unit,
    isSortAscending: Boolean,
    onSortOrderChange: (ascending: Boolean) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    PrimaryTopAppBar(
        title = stringResource(Res.string.applications),
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                DropdownMenuItem(
                    text = {
                        CpuSwitchBox(
                            text = stringResource(Res.string.apps_show_system_apps),
                            isChecked = withSystemApps,
                            onCheckedChange = { onSystemAppsSwitched(!withSystemApps) },
                        )
                    },
                    onClick = { onSystemAppsSwitched(!withSystemApps) },
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(Res.string.apps_sort_order),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    onClick = { onSortOrderChange(!isSortAscending) },
                    trailingIcon = {
                        val icon = if (isSortAscending) {
                            Icons.Default.KeyboardArrowDown
                        } else {
                            Icons.Default.KeyboardArrowUp
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                        )
                    },
                )
            }
        },
    )
}*/

/*@Composable
private fun ApplicationsList(
    appList: ImmutableList<ExtendedApplicationData>,
    onAppClicked: (packageName: String) -> Unit,
    onAppUninstallClicked: (id: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()
        var revealedCardId: String? by rememberSaveable { mutableStateOf(null) }
        val density = LocalDensity.current
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            itemsIndexed(
                items = appList,
                key = { _, item -> item.packageName },
            ) { index, item ->
                val isRevealed by remember {
                    derivedStateOf { revealedCardId == item.packageName }
                }
                DraggableBox(
                    isRevealed = isRevealed,
                    onExpand = { revealedCardId = item.packageName },
                    onCollapse = {
                        if (revealedCardId == item.packageName) {
                            revealedCardId = null
                        }
                    },
                    actionRowOffset = with(density) { rowActionIconSize.toPx() * 2 },
                    actionRow = {
                        Row {
                            IconButton(
                                modifier = Modifier.requiredSize(rowActionIconSize),
                                onClick = { onAppSettingsClicked(item.packageName) },
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_settings),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = stringResource(Res.string.settings),
                                )
                            }
                            IconButton(
                                modifier = Modifier.requiredSize(rowActionIconSize),
                                onClick = { onAppUninstallClicked(item.packageName) },
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_thrash),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                    content = {
                        ApplicationItem(
                            appData = item,
                            onAppClicked = onAppClicked,
                            onNativeLibsClicked = onNativeLibsClicked,
                        )
                    },
                    modifier = Modifier.animateItem(),
                )
                if (index < appList.lastIndex) {
                    CpuDivider(
                        modifier = Modifier.padding(horizontal = spacingSmall),
                    )
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            scrollState = listState,
        )
    }
}

@Composable
private fun ApplicationItem(
    appData: ExtendedApplicationData,
    onAppClicked: (packageName: String) -> Unit,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .clickable(onClick = { onAppClicked(appData.packageName) })
            .padding(spacingSmall),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(appData.appIconUri)
                .crossfade(true)
                .build(),
            contentDescription = appData.name,
            modifier = Modifier.size(50.dp),
        )
        SelectionContainer {
            Column(
                modifier = Modifier.padding(horizontal = spacingXSmall),
            ) {
                Text(
                    text = appData.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = appData.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        if (appData.hasNativeLibs) {
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(spacingXSmall))
            IconButton(
                onClick = { onNativeLibsClicked(appData.nativeLibs) },
                modifier = Modifier.requiredSize(rowActionIconSize),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_cpp_logo),
                    contentDescription = stringResource(Res.string.native_libs),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(spacingSmall),
                )
            }
        }
    }
}*/
