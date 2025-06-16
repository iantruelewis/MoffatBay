# Moffat Bay Lodge – SQL Database Setup

This project sets up a relational database for Moffat Bay Lodge — a high-end lodge and marina. It includes user management, reservations, rooms, and contact forms.

Files named in sequential order of operations. Process described below.

## Files

| File | Purpose |
|------|---------|
| `01_moffat_bay_lodge_schema.sql` | Creates all tables for the Moffat Bay Lodge system |
| `02_insert_sample_data.sql` | Seeds the database with sample records for testing |
| `03_verify_contents.sql` | SELECTs all records so you can screenshot for documentation |

## How to Run

From the command line:

```bash
psql -f 01_moffat_bay_lodge_schema.sql
psql -f 02_insert_sample_data.sql
psql -f 03_verify_contents.sql