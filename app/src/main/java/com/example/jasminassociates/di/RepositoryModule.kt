package com.example.jasminassociates.di


import com.example.jasminassociates.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://wtwgudfugmvupfmtycfq.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind0d2d1ZGZ1Z212dXBmbXR5Y2ZxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3MDA3NjYsImV4cCI6MjA3ODI3Njc2Nn0.wyPPgzOOG6jYXGt8kWzrm4Kcx8LX9xAIv4ncB37XvdQ"
        ) {
            install(Postgrest)
        }
    }

    @Provides
    @Singleton
    fun provideUserRepository(client: SupabaseClient): UserRepository {
        return UserRepository(client)
    }

    @Provides
    @Singleton
    fun provideProjectRepository(client: SupabaseClient): ProjectRepository {
        return ProjectRepository(client)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(client: SupabaseClient): TaskRepository {
        return TaskRepository(client)
    }

    @Provides
    @Singleton
    fun provideEquipmentRepository(client: SupabaseClient): EquipmentRepository {
        return EquipmentRepository(client)
    }

    @Provides
    @Singleton
    fun provideEquipmentRequestRepository(client: SupabaseClient): EquipmentRequestRepository {
        return EquipmentRequestRepository(client)
    }

    @Provides
    @Singleton
    fun provideEquipmentAssignmentRepository(client: SupabaseClient): EquipmentAssignmentRepository {
        return EquipmentAssignmentRepository(client)
    }

    @Provides
    @Singleton
    fun provideDamageReportRepository(client: SupabaseClient): DamageReportRepository {
        return DamageReportRepository(client)
    }

    @Provides
    @Singleton
    fun provideSecurityShiftRepository(client: SupabaseClient): SecurityShiftRepository {
        return SecurityShiftRepository(client)
    }

    @Provides
    @Singleton
    fun provideInvoiceRepository(client: SupabaseClient): InvoiceRepository {
        return InvoiceRepository(client)
    }

    @Provides
    @Singleton
    fun provideAnalyticsRepository(client: SupabaseClient): AnalyticsRepository {
        return AnalyticsRepository(client)
    }
}