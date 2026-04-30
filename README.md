# 🏦 Transactional Bank System
 
> A scalable transactional banking backend that handles the full transaction lifecycle — from initiation and authorization to settlement and posting.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?logo=springboot)
![Kafka](https://img.shields.io/badge/Apache_Kafka-3.x-black?logo=apachekafka)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7+-red?logo=redis)
![License](https://img.shields.io/badge/License-MIT-yellow)
 
---
 
## 📋 Table of Contents
 
- [Overview](#-overview)
- [Architecture](#-architecture)
- [Current Features](#-current-features)
- [Roadmap & Future Plans](#-roadmap--future-plans)
  - [Core Banking Operations](#1-core-banking-operations)
  - [Fund Transfers](#2-fund-transfers)
  - [Loan Management](#3-loan-management)
  - [Mortgage Module](#4-mortgage-module)
  - [Deposit & Savings Products](#5-deposit--savings-products)
  - [Payment Gateway Expansion](#6-payment-gateway-expansion)
  - [Compliance & Auditing](#7-compliance--auditing)
  - [Notifications & Reporting](#8-notifications--reporting)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [License](#-license)
---
 
## 🔍 Overview
 
Transactional Bank System is a backend platform designed to power modern digital banking operations. It follows a **CQRS (Command Query Responsibility Segregation)** pattern, separating write operations (`command-service`) from read operations (`query-service`) to ensure high availability and data consistency at scale.
 
The system is built to support the full financial transaction lifecycle: account creation, fund movement, settlement, reconciliation, and audit logging — all with event-driven architecture powered by Apache Kafka.
 
---
 
## 🏗️ Architecture
 
```
┌────────────────────────────────────────────────────────────┐
│                        API Gateway                         │
└──────────────┬───────────────────────────┬─────────────────┘
               │                           │
     ┌─────────▼──────────┐     ┌──────────▼─────────┐
     │   command-service  │     │    query-service   │
     │  (Writes / CQRS)   │     │   (Reads / CQRS)   │
     └─────────┬──────────┘     └──────────┬─────────┘
               │                           │
     ┌─────────▼───────────────────────────▼─────────┐
     │               Apache Kafka                    │
     │         (Event Streaming / Messaging)         │
     └─────────┬──────────────────────────┬──────────┘
               │                          │
     ┌─────────▼──────────┐    ┌──────────▼──────────┐
     │     PostgreSQL     │    │        Redis        │
     │  (Primary Store)   │    │  (Cache / Sessions) │
     └────────────────────┘    └─────────────────────┘
```
 
---
 
## ✅ Current Features
 
- **Account Management** — Create and manage customer bank accounts
- **Transaction Lifecycle** — Full pipeline from initiation → authorization → settlement → posting
- **Reconciliation Engine** — Automated matching and balancing of transactions
- **Payment Gateway Integration** — External payment processing support
- **Event-Driven Notifications** — Kafka-powered async notification system
- **Immutable Transaction Logs** — Tamper-proof audit trail for compliance
- **Authentication & Authorization** — Secure access control
---
 
## 🚀 Roadmap & Future Plans
 
### 1. Core Banking Operations
 
| Feature | Description | Priority |
|---|---|---|
| Multi-Currency Accounts | Support accounts in AZN, USD, EUR and other currencies with live exchange rates | High |
| Account Freezing / Blocking | Temporarily or permanently freeze accounts flagged for suspicious activity | High |
| Balance Inquiry API | Real-time balance checks with available vs. ledger balance distinction | Medium |
| Account Statements | Generate monthly/custom-period statements in PDF and CSV formats | Medium |
| Overdraft Protection | Configurable overdraft limits with automatic fee application | Low |
 
---
 
### 2. Fund Transfers
 
A comprehensive fund transfer module covering both domestic and international movement of money.
 
#### 2.1 Domestic Transfers
- **Internal Transfer** — Move funds between accounts within the same bank instantly
- **Interbank Transfer** — Send money to accounts at other Azerbaijani banks via **AZIPS** (Azerbaijan Interbank Payment System) and **SWIFT** local routing
- **Scheduled Transfers** — Set up one-time or recurring transfers on a defined schedule
- **Bulk / Batch Payments** — Upload and process a batch of transfers from a CSV/Excel file (useful for payroll)
#### 2.2 International Transfers (SWIFT)
- SWIFT MT103 message generation and parsing
- Correspondent bank routing
- Intermediary fee handling and beneficiary deduction options
- Real-time SWIFT status tracking
#### 2.3 Instant Payments
- Integration with **PAYM** (instant payment rail)
- Sub-second settlement for supported corridors
- 24/7 availability including weekends and public holidays
#### 2.4 Transfer Limits & Controls
- Daily, weekly, and monthly transfer caps per account tier
- Two-factor authentication (OTP/TOTP) for large transfers
- Beneficiary whitelist management
---
 
### 3. Loan Management
 
End-to-end loan origination, servicing, and collection within the platform.
 
#### 3.1 Loan Types
- **Consumer Loans** — Personal unsecured loans with fixed/variable rates
- **Auto Loans** — Vehicle financing with collateral registration
- **Business Loans** — SME lending with flexible repayment structures
- **Microloans** — Small-ticket loans with simplified KYC and fast approval
#### 3.2 Loan Lifecycle
```
Application → Underwriting → Approval/Rejection → Disbursement
     → Repayment Schedule → Installment Collection → Closure
```
 
- Automated credit scoring via integration with **Azerbaijani Credit Bureau (AKB)**
- Configurable repayment schedules: annuity, differentiated, bullet
- Early repayment support with dynamic penalty/fee calculation
- Loan restructuring and refinancing workflows
- Non-performing loan (NPL) flagging and collections pipeline
#### 3.3 Interest & Fee Engine
- Support for fixed and floating interest rates
- Compound and simple interest calculation modes
- Late payment penalty rules with grace period configuration
- Fee schedules: origination fee, service fee, early closure fee
---
 
### 4. Mortgage Module
 
A dedicated **Mortgage** module aligned with Azerbaijani housing finance regulations and the programs offered by the **Azerbaijan Mortgage and Credit Guarantee Fund (AMKQ)**.
 
#### 4.1 Mortgage Products
 
| Product | Description |
|---|---|
| Standard Mortgage | Long-term home purchase loan (up to 30 years) at market rates |
| Subsidized Mortgage | Government-backed preferential rate mortgages (AMKQ programs) |
| Renovation Loan | Financing for home improvement and refurbishment |
| Construction Loan | Stage-disbursement loan for new-build properties |
| Re-mortgage | Refinancing existing mortgages from other lenders |
 
#### 4.2 Application & Underwriting
- Online mortgage application form with document upload (passport, income certificate, property documents)
- Automated LTV (Loan-to-Value) ratio calculation
- Property valuation workflow — integration with certified appraisers
- DTI (Debt-to-Income) ratio checks
- Credit history pull from AKB (Azerbaijan Credit Bureau)
- AMKQ eligibility checker for subsidized program applicants
#### 4.2 Disbursement & Collateral Management
- Staged disbursement for construction mortgages
- Collateral registration tracking (linked to the State Registry of Real Estate — Daşınmaz Əmlak Reyestri)
- Automatic lien/encumbrance status updates
- Title insurance integration
#### 4.3 Repayment & Servicing
- Annuity and differentiated repayment schedules
- Automated monthly installment collection via direct debit
- Partial prepayment processing with recalculated amortization schedule
- Full early repayment with penalty-free periods (aligned with AMKQ rules)
- Mortgage statement generation: remaining balance, interest paid to date, next installment
#### 4.4 Insurance Integration
- Mandatory property insurance tracking (fire, natural disaster)
- Life insurance (borrower protection) policy linking
- Insurance expiry alerts and renewal reminders
#### 4.5 Regulatory & Reporting
- Reports for the Central Bank of Azerbaijan (CBAR) — mortgage portfolio reporting
- AMKQ subsidy reconciliation reports
- Non-performing mortgage tracking and reporting
- Foreclosure workflow initiation (legal process trigger)
---
 
### 5. Deposit & Savings Products
 
#### 5.1 Deposit Types
- **Demand Deposits** — Standard current/checking accounts with no lock-in
- **Term Deposits** — Fixed-term deposits with guaranteed interest rates (30 days to 3 years)
- **Savings Accounts** — Higher-yield accounts with limited monthly withdrawals
- **Foreign Currency Deposits** — AZN, USD, EUR term deposits with currency-matching interest
#### 5.2 Deposit Features
- Automatic rollover (with or without interest capitalization)
- Early termination with partial interest retention
- Deposit top-up during the term
- Interest payment options: monthly, quarterly, at maturity
- ADIF (Azerbaijan Deposit Insurance Fund) coverage calculation and disclosure
---
 
### 6. Payment Gateway Expansion
 
- **Card Payments** — Visa / Mastercard acquiring and issuing support
- **QR Code Payments** — Generate and scan QR codes for merchant payments
- **ASAN Payment** integration — Azerbaijan's national e-government payment rail
- **Utility Bill Payments** — Electricity (Azerenerji), gas, water, internet via aggregator APIs
- **Mobile Top-up** — Azercell, Bakcell, Nar Mobile airtime recharge
- **Tax Payments** — Direct integration with Azerbaijan State Tax Service (VÖEN payments)
- **E-Commerce Checkout API** — Embeddable payment widget for merchant websites
---
 
### 7. Compliance & Auditing
 
- **AML (Anti-Money Laundering)** — Rule-based and ML-driven suspicious transaction detection
- **KYC (Know Your Customer)** — Digital identity verification with ASAN İmza / FIN integration
- **FATF Compliance** — Screening against international sanctions lists (OFAC, UN, EU)
- **CBAR Reporting** — Automated regulatory reports for the Central Bank of Azerbaijan
- **GDPR / Data Privacy** — Configurable data retention and right-to-erasure support
- **Immutable Audit Logs** — All actions stored in an append-only, cryptographically signed log
---
 
### 8. Notifications & Reporting
 
- Push notifications (mobile), email, and SMS for all transaction events
- Configurable alert thresholds (e.g., transaction above X AZN)
- Monthly account summary reports
- Analytics dashboard for internal bank operations
- Export to XBRL for Central Bank submissions
---
 
## 🛠 Tech Stack
 
| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Messaging | Apache Kafka |
| Database | PostgreSQL |
| Cache | Redis |
| Auth | Spring Security + JWT |
| Architecture | CQRS / Event-Driven |
| Build | Maven / Gradle |
| Containerization | Docker + Docker Compose |
 
---
 
## 🚀 Getting Started
 
### Prerequisites
 
- Java 21+
- Docker & Docker Compose
- PostgreSQL 15+
- Apache Kafka
### Running Locally
 
```bash
# Clone the repository
git clone https://github.com/yourfavouritedeveloper/TransactionalBankSystem.git
cd TransactionalBankSystem
 
# Start infrastructure (Kafka, PostgreSQL, Redis)
docker-compose up -d
 
# Run command service
cd command-service
./mvnw spring-boot:run
 
# Run query service (new terminal)
cd query-service
./mvnw spring-boot:run
```
 
---
 
## 📄 License
 
This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
 
© 2026 Nihad Mammadov
 
---
 
> *"Banking is not just about money. It's about trust."*
