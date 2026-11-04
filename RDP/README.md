# RDP Secure Host Setup

هذا المجلد يجهّز جهاز Windows تملكه لاستقبال اتصالات Remote Desktop عبر شبكة خاصة. لا ينشئ خادم Windows سحابياً، ولا يستخدم GitHub Actions كخدمة استضافة أو لتجاوز حدود الجلسات.

## المتطلبات
- جهاز Windows Pro / Enterprise / Education يعمل ويظل متصلاً بالإنترنت.
- حساب Windows بكلمة مرور قوية.
- تثبيت Tailscale على جهاز Windows وعلى هاتف Android، وتسجيل الجهازين في حساب Tailscale نفسه.
- لا تفتح المنفذ 3389 على الإنترنت ولا تضف Port Forwarding في الراوتر.

## الإعداد
1. نزّل هذا المستودع على جهاز Windows.
2. افتح PowerShell بصلاحية المسؤول.
3. شغّل:
   `powershell -ExecutionPolicy Bypass -File .\RDP\Enable-RDP.ps1`
4. ثبّت Tailscale من الموقع الرسمي على الكمبيوتر والهاتف، وسجّل الدخول بالحساب نفسه.
5. اعرف عنوان Tailscale الخاص بالكمبيوتر من تطبيق Tailscale.
6. على Android افتح تطبيق Windows App (أو عميل RDP موثوق)، وأضف اتصالاً بعنوان Tailscale IP واسم مستخدم Windows وكلمة مروره.

## حدود مهمة
- Windows Home لا يستضيف جلسات RDP الواردة بشكل رسمي؛ استخدم Windows Pro أو جهازاً يدعم Remote Desktop Host.
- يجب أن يبقى الكمبيوتر قيد التشغيل ومتصلًا بالشبكة.
- GitHub Actions runners مؤقتة ومخصصة لأتمتة البناء والاختبار، وليست خوادم RDP دائمة.
- لا تضع كلمات المرور أو مفاتيح Tailscale أو مفاتيح API في المستودع أو GitHub Issues.
- لا تشارك عنوان Tailscale أو بيانات الدخول مع أي شخص غير موثوق.

## تعطيل RDP
شغّل PowerShell كمسؤول:
`Set-ItemProperty -Path 'HKLM:\\SYSTEM\\CurrentControlSet\\Control\\Terminal Server' -Name fDenyTSConnections -Value 1`
ثم عطّل قواعد جدار الحماية:
`Disable-NetFirewallRule -DisplayGroup 'Remote Desktop'`
