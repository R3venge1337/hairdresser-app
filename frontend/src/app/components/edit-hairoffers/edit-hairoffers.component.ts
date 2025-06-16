import { Component, OnInit, signal } from '@angular/core';
import { HairoffersService } from '../../services/hairoffers.service';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatError, MatFormFieldModule } from '@angular/material/form-field';
import { Hairoffers } from '../../models/hairoffers';
import { MatInputModule } from '@angular/material/input';
import Big from 'big.js';
import { ToastrService } from 'ngx-toastr';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-edit-hairoffers',
  imports: [
    CommonModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatError,
    ReactiveFormsModule,
    FormsModule,
  ],
  templateUrl: './edit-hairoffers.component.html',
  styleUrl: './edit-hairoffers.component.css',
})
export class EditHairoffersComponent implements OnInit {
  updateHairOfferForm: FormGroup;
  hairOffer = signal<Hairoffers>({
    id: 0,
    name: '',
    description: '',
    price: Big(0),
    duration: 0,
  });

  id!: number;
  router!: Router;

  constructor(
    private route: ActivatedRoute,
    private hairOfferService: HairoffersService,
    private fb: FormBuilder,
    private toastr: ToastrService,
    router: Router
  ) {
    this.router = router;
    this.updateHairOfferForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      duration: [0, [Validators.required, Validators.min(0)]],
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.id = Number(id);
      this.getHairOffer(this.id);
    }
  }

  async getHairOffer(id: number): Promise<void> {
    try {
      const offer = await this.hairOfferService.getHairOfferById(id);
      this.updateHairOfferForm.patchValue({
        name: offer.name,
        description: offer.description,
        price: offer.price,
        duration: offer.duration,
      });
    } catch (error) {
      console.error('Error fetching hair offer:', error);
    }
  }

  async updateHairOffer(): Promise<void> {
    if (this.updateHairOfferForm.valid) {
      try {
        await this.hairOfferService.updateHairOffer(
          this.id,
          this.updateHairOfferForm.value
        );
        this.router.navigate(['/hairoffers']); // Navigate back after update
      } catch (error) {
        console.error('Error updating hair offer:', error);
      }
    } else {
      this.toastr.error('Please fill in all required fields');
    }
  }
}
