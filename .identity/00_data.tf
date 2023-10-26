data "azurerm_resource_group" "dashboards" {
  name = "dashboards"
}

data "azurerm_kubernetes_cluster" "aks" {
  name                = local.aks_cluster.name
  resource_group_name = local.aks_cluster.resource_group_name
}

data "github_organization_teams" "all" {
  root_teams_only = true
  summary_only    = true
}

data "azurerm_key_vault" "key_vault" {
  name                = "pagopa-${var.env_short}-kv"
  resource_group_name = "pagopa-${var.env_short}-sec-rg"
}

data "azurerm_key_vault" "key_vault_domain" {
  name                = "pagopa-${var.env_short}-${local.domain}-kv"
  resource_group_name = "pagopa-${var.env_short}-${local.domain}-sec-rg"
}

data "azurerm_key_vault_secret" "key_vault_sonar" {
  name         = "sonar-token"
  key_vault_id = data.azurerm_key_vault.key_vault.id
}

data "azurerm_key_vault_secret" "key_vault_bot_token" {
  name         = "bot-token-github"
  key_vault_id = data.azurerm_key_vault.key_vault.id
}

data "azurerm_key_vault_secret" "key_vault_cucumber_token" {
  name         = "cucumber-token"
  key_vault_id = data.azurerm_key_vault.key_vault.id
}

data "azurerm_storage_account" "receipts_sa" {
  name                = "pagopa${var.env_short}${local.location_short}receiptsfnsa"
  resource_group_name = "pagopa-${var.env_short}-${local.location_short}-receipts-st-rg"
}

data "azurerm_cosmosdb_account" "receipts_cosmos" {
  name                = "pagopa-${var.env_short}-${local.location_short}-receipts-ds-cosmos-account"
  resource_group_name = "pagopa-${var.env_short}-${local.location_short}-receipts-rg"
}

data "azurerm_key_vault_secret" "key_vault_integration_test_subkey" {
  name         = "apikey-service-receipt" # "integration-test-subkey"
  key_vault_id = data.azurerm_key_vault.key_vault_domain.id
}

data "azurerm_key_vault_secret" "key_vault_integration_test_webhook_slack" {
  name         = "webhook-slack"
  key_vault_id = data.azurerm_key_vault.key_vault_domain.id
}