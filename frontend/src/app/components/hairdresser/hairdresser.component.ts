import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { HairdresserService } from '../../services/hairdresser.service';
import { HairdresserDto } from '../../models/hairdresser-dto';
import { ToastrService } from 'ngx-toastr';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-hairdresser',
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    TranslateModule,
    MatDividerModule,
    ReactiveFormsModule,
  ],
  templateUrl: './hairdresser.component.html',
  styleUrl: './hairdresser.component.css',
})
export class HairdresserComponent implements OnInit {
  hairdressers: WritableSignal<HairdresserDto[]> = signal([]);
  showAddHairdresserForm: boolean = false; // Kontroluje widoczność formularza
  registerForm!: FormGroup; // Formularz do dodawania fryzjera

  displayedColumns: string[] = ['uuid', 'firstname', 'surname', 'phoneNumber'];

  constructor(
    private hairdresserService: HairdresserService,
    private toastService: ToastrService,
    private fb: FormBuilder,
    public authService: AuthService
  ) {
    this.registerForm = this.fb.group({
      firstname: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      password: ['', Validators.required],
      phoneNumber: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.getAllHairdressers();
  }

  async getAllHairdressers(): Promise<void> {
    try {
      const data: HairdresserDto[] =
        await this.hairdresserService.getAllUsersByRole('HAIRDRESSER');
      this.hairdressers.set(data);
      this.toastService.success('Hairdressers loaded:');
      console.log('Hairdressers loaded:', this.hairdressers());
    } catch (error) {
      this.toastService.error('Hairdressers loaded:');
      console.error('Error loading hairdressers:', error);
    }
  }

  toggleAddHairdresserForm(): void {
    this.showAddHairdresserForm = !this.showAddHairdresserForm;
    if (!this.showAddHairdresserForm) {
      this.registerForm.reset(); // Zresetuj formularz, gdy jest ukrywany
    }
  }

  async onSubmit(): Promise<void> {
    if (this.registerForm.valid) {
      console.log('Form data:', this.registerForm.value);
      try {
        await this.hairdresserService.addHairdresser(this.registerForm.value);
        this.toastService.success('Dodanie fryzjera przebiegło poprawnie');

        this.registerForm.reset(); // Wyczyść formularz po sukcesie
        this.showAddHairdresserForm = false; // Ukryj formularz
        this.getAllHairdressers(); // Odśwież listę fryzjerów
      } catch (error) {
        console.error('Error adding hairdresser:', error);
        this.toastService.error(
          'Failed to add hairdresser. Please check console for details.'
        );
      }
    } else {
      this.registerForm.markAllAsTouched();
      console.error('Form is invalid.');
    }
  }
}
