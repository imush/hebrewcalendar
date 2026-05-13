package net.hebrewcalendar;

/**
 * Length type of the Hebrew year, determined by the number of days in Cheshvan and Kislev:
 * SHORT  — both 29 days
 * NORMAL — Cheshvan 29, Kislev 30
 * FULL   — both 30 days
 */
public enum YearCheshvanKislevType {
    SHORT, NORMAL, FULL
}
