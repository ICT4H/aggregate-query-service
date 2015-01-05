{
    "dataSet": "${dataset}",
    "period": "${period}",
    "orgUnit": "${organization}",
    "dataValues": [
<#list Query_19 as row>
    {
    "dataElement": "AiPqHCbJQJ1",
    "categoryOptionCombo": "u2QXNMacZLt",
    "value": "${row.v1}"
    },
    {
    "dataElement": "AiPqHCbJQJ1",
    "categoryOptionCombo": "UBdaznQ8DlT",
    "value": "${row.v3}"
    },
    {
    "dataElement": "AiPqHCbJQJ2",
    "categoryOptionCombo": "KahybAysMCQ",
    "value": "${row.v6}"
    }
</#list>
]
}