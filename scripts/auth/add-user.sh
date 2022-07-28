/opt/keycloak/bin/kcadm.sh \
    config credentials \
    --server http://localhost:8080 \
    --realm master \
    --user myuser \
    --password mypass

/opt/keycloak/bin/kcadm.sh \
    create users \
    -r demo-realm \
    -s username=demo-user \
    -s enabled=true

/opt/keycloak/bin/kcadm.sh \
    set-password \
    -r demo-realm \
    --username demo-user \
    --new-password demo-pass
