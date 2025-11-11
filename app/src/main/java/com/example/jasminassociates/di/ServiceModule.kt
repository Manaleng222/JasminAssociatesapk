package com.example.jasminassociates.di

import android.content.Context
import com.example.jasminassociates.services.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideLocationService(@ApplicationContext context: Context): LocationService {
        return LocationService(context)
    }

    @Provides
    @Singleton
    fun provideAuthService(userRepository: com.example.jasminassociates.data.repository.UserRepository): AuthService {
        return AuthService(userRepository)
    }

    @Provides
    @Singleton
    fun provideDashboardService(
        userRepository: com.example.jasminassociates.data.repository.UserRepository,
        projectRepository: com.example.jasminassociates.data.repository.ProjectRepository,
        invoiceRepository: com.example.jasminassociates.data.repository.InvoiceRepository,
        taskRepository: com.example.jasminassociates.data.repository.TaskRepository,
        equipmentRepository: com.example.jasminassociates.data.repository.EquipmentRepository
    ): DashboardService {
        return DashboardService(userRepository, projectRepository, invoiceRepository, taskRepository, equipmentRepository)
    }

    @Provides
    @Singleton
    fun provideEquipmentRequestService(
        equipmentRequestRepository: com.example.jasminassociates.data.repository.EquipmentRequestRepository,
        equipmentRepository: com.example.jasminassociates.data.repository.EquipmentRepository,
        equipmentAssignmentRepository: com.example.jasminassociates.data.repository.EquipmentAssignmentRepository
    ): EquipmentRequestService {
        return EquipmentRequestService(equipmentRequestRepository, equipmentRepository, equipmentAssignmentRepository)
    }

    @Provides
    @Singleton
    fun provideEquipmentService(equipmentRepository: com.example.jasminassociates.data.repository.EquipmentRepository): EquipmentService {
        return EquipmentService(equipmentRepository)
    }

    @Provides
    @Singleton
    fun provideInvoiceService(invoiceRepository: com.example.jasminassociates.data.repository.InvoiceRepository): InvoiceService {
        return InvoiceService(invoiceRepository)
    }

    @Provides
    @Singleton
    fun provideProjectService(projectRepository: com.example.jasminassociates.data.repository.ProjectRepository): ProjectService {
        return ProjectService(projectRepository)
    }

    @Provides
    @Singleton
    fun provideProjectTaskService(taskRepository: com.example.jasminassociates.data.repository.TaskRepository): ProjectTaskService {
        return ProjectTaskService(taskRepository)
    }

    @Provides
    @Singleton
    fun provideSecurityShiftService(
        shiftRepository: com.example.jasminassociates.data.repository.SecurityShiftRepository,
        locationService: LocationService
    ): SecurityShiftService {
        return SecurityShiftService(shiftRepository, locationService)
    }

    @Provides
    @Singleton
    fun provideUserService(userRepository: com.example.jasminassociates.data.repository.UserRepository): UserService {
        return UserService(userRepository)
    }
}