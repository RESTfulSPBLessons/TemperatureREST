import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChartsModule } from 'ng2-charts/ng2-charts';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ButtonsModule } from 'ngx-bootstrap/buttons';
 import {HttpClientModule} from '@angular/common/http';
import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import {CommonModule} from '@angular/common';


@NgModule({
  imports: [
    FormsModule,
    DashboardRoutingModule,
    ChartsModule,
     HttpClientModule,
    CommonModule,
    BsDropdownModule,
    ButtonsModule.forRoot()
  ],
  declarations: [ DashboardComponent ]
})
export class DashboardModule { }
