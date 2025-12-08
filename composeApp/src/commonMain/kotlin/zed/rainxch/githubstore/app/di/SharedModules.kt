package zed.rainxch.githubstore.app.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import zed.rainxch.githubstore.MainViewModel
import zed.rainxch.githubstore.core.data.data_source.DefaultTokenDataSource
import zed.rainxch.githubstore.core.data.data_source.TokenDataSource
import zed.rainxch.githubstore.core.data.repository.ThemesRepositoryImpl
import zed.rainxch.githubstore.core.domain.getPlatform
import zed.rainxch.githubstore.core.domain.repository.ThemesRepository
import zed.rainxch.githubstore.network.buildAuthedGitHubHttpClient
import zed.rainxch.githubstore.feature.auth.data.repository.AuthRepositoryImpl
import zed.rainxch.githubstore.feature.auth.domain.*
import zed.rainxch.githubstore.feature.auth.domain.repository.AuthRepository
import zed.rainxch.githubstore.feature.auth.presentation.AuthenticationViewModel
import zed.rainxch.githubstore.feature.details.data.repository.DetailsRepositoryImpl
import zed.rainxch.githubstore.feature.details.domain.repository.DetailsRepository
import zed.rainxch.githubstore.feature.details.presentation.DetailsViewModel
import zed.rainxch.githubstore.feature.details.data.Downloader
import zed.rainxch.githubstore.feature.details.data.Installer
import zed.rainxch.githubstore.feature.home.data.repository.HomeRepositoryImpl
import zed.rainxch.githubstore.feature.home.domain.repository.HomeRepository
import zed.rainxch.githubstore.feature.home.presentation.HomeViewModel
import zed.rainxch.githubstore.feature.search.data.repository.SearchRepositoryImpl
import zed.rainxch.githubstore.feature.search.domain.repository.SearchRepository
import zed.rainxch.githubstore.feature.search.presentation.SearchViewModel
import zed.rainxch.githubstore.feature.settings.presentation.SettingsViewModel

val coreModule: Module = module {
    single<TokenDataSource> {
        DefaultTokenDataSource(
            tokenStore = get()
        )
    }

    single {
        buildAuthedGitHubHttpClient(
            tokenDataSource = get()
        )
    }

    single<ThemesRepository> {
        ThemesRepositoryImpl(
            preferences = get()
        )
    }

    viewModelOf(::MainViewModel)
}

val authModule: Module = module {
    single<AuthRepository> { AuthRepositoryImpl(tokenDataSource = get()) }

    factory { StartDeviceFlowUseCase(get()) }
    factory { AwaitDeviceTokenUseCase(get()) }
    factory { ObserveAccessTokenUseCase(get()) }
    factory { LogoutUseCase(get()) }

    viewModelOf(::AuthenticationViewModel)
}

val homeModule: Module = module {
    single<HomeRepository> {
        HomeRepositoryImpl(
            githubNetworkClient = get(),
            platform = getPlatform()
        )
    }

    viewModel { HomeViewModel(get(), get()) }
}

val searchModule: Module = module {
    single<SearchRepository> {
        SearchRepositoryImpl(
            githubNetworkClient = get(),
        )
    }

    viewModel { SearchViewModel(get()) }
}

val detailsModule: Module = module {
    single<DetailsRepository> {
        DetailsRepositoryImpl(github = get())
    }

    viewModel { params ->
        DetailsViewModel(
            repositoryId = params.get(),
            detailsRepository = get(),
            downloader = get<Downloader>(),
            installer = get<Installer>(),
            platform = getPlatform(),
            helper = get()
        )
    }
}

val settingsModule: Module = module {
    viewModelOf(::SettingsViewModel)
}
