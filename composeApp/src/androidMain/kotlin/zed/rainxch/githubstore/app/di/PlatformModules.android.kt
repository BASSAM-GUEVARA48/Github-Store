package zed.rainxch.githubstore.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import zed.rainxch.githubstore.core.data.AndroidApkInfoExtractor
import zed.rainxch.githubstore.core.data.AndroidPackageMonitor
import zed.rainxch.githubstore.core.data.PackageMonitor
import zed.rainxch.githubstore.core.data.local.data_store.createDataStore
import zed.rainxch.githubstore.core.data.local.db.AppDatabase
import zed.rainxch.githubstore.core.data.local.db.initDatabase
import zed.rainxch.githubstore.core.presentation.utils.AndroidAppLauncher
import zed.rainxch.githubstore.core.presentation.utils.AndroidBrowserHelper
import zed.rainxch.githubstore.core.presentation.utils.AndroidClipboardHelper
import zed.rainxch.githubstore.core.presentation.utils.AppLauncher
import zed.rainxch.githubstore.core.presentation.utils.BrowserHelper
import zed.rainxch.githubstore.core.presentation.utils.ClipboardHelper
import zed.rainxch.githubstore.feature.apps.domain.repository.AppsRepository
import zed.rainxch.githubstore.feature.auth.data.AndroidTokenStore
import zed.rainxch.githubstore.feature.auth.data.TokenStore
import zed.rainxch.githubstore.feature.details.data.AndroidDownloader
import zed.rainxch.githubstore.feature.details.data.AndroidFileLocationsProvider
import zed.rainxch.githubstore.feature.details.data.AndroidInstaller
import zed.rainxch.githubstore.feature.details.data.Downloader
import zed.rainxch.githubstore.feature.details.data.FileLocationsProvider
import zed.rainxch.githubstore.feature.details.data.Installer

actual val platformModule: Module = module {
    single<Downloader> {
        AndroidDownloader(
            context = get(),
            files = get()
        )
    }

    single<Installer> {
        AndroidInstaller(
            context = get(),
            apkInfoExtractor = AndroidApkInfoExtractor(androidContext())
        )
    }

    single<FileLocationsProvider> {
        AndroidFileLocationsProvider(context = get())
    }

    single<DataStore<Preferences>> {
        createDataStore(androidContext())
    }

    single<BrowserHelper> {
        AndroidBrowserHelper(androidContext())
    }

    single<ClipboardHelper> {
        AndroidClipboardHelper(androidContext())
    }

    single<TokenStore> {
        AndroidTokenStore(
            dataStore = get()
        )
    }

    single<AppDatabase> {
        initDatabase(androidContext())
    }

    single<PackageMonitor> {
        AndroidPackageMonitor(androidContext())
    }

    single<AppLauncher> {
        AndroidAppLauncher(androidContext())
    }
}