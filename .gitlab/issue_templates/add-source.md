### URL

### Description
(Which source of Open Educational Resources should be connected?)

### Access Point
(How can the source be accessed? Please add API/OAI-PMH/Sitemap documentation and/or an example)

### Contact
(Email, name of contact person etc.)

### Tasks

- [ ] Contact provider
- [ ] Clarify details, receive permission
- [ ] Configure ETL process (see [Checklist](#Checklist))
- [ ] Publish blog post
- [ ] Inform provider

### Checklist

#### Mandatory in AMB/OERSI

- [ ] `@context`
    - [ ] `@language` set to the record's language?
- [ ] `id`
- [ ] `"type": "LearningResource"`
- [ ] `name`
- [ ] `mainEntityOfPage` with `provider`

#### Essential data for filtering

- [ ] `learningResourceType`
- [ ] `creator`, ideally with `id`
- [ ] `about`
- [ ] `license`
- [ ] `inlanguage`
- [ ] `sourceOrganization`

#### Important for display purposes

- [ ] `image`

#### Additional metadata

- [ ] `description`
- [ ] `keywords`
- [ ] `datePublished` and/or `dateCreated`
- [ ] `encoding.contentUrl` for download links
- [ ] `encoding.embedUrl` for embed links

/label add-source
